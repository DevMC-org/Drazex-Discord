/*
 *  Drazex-Discord
 *  Discord-bot for the project community devmc.org,
 *  designed to automate administrative tasks, notifications
 *  and other functionality related to the functioning of the community
 *  Copyright (C) 2023 Ivan `Xezard` Zotov
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package me.xezard.devmc.drazex.discord.service.commands.handlers

import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent
import discord4j.core.`object`.command.ApplicationCommandOption
import discord4j.core.`object`.command.ApplicationCommandPermission
import discord4j.discordjson.json.ApplicationCommandOptionChoiceData
import discord4j.discordjson.json.ApplicationCommandOptionData
import discord4j.discordjson.json.ApplicationCommandPermissionsData
import discord4j.discordjson.json.ApplicationCommandRequest
import me.xezard.devmc.drazex.discord.domain.model.request.RequestType
import me.xezard.devmc.drazex.discord.service.commands.ICommandHandler
import me.xezard.devmc.drazex.discord.service.modals.IModalHandler
import me.xezard.devmc.drazex.discord.service.modals.handlers.ExecutorSearchModalHandler
import me.xezard.devmc.drazex.discord.service.modals.handlers.TeamRecruitmentModalHandler
import me.xezard.devmc.drazex.discord.service.modals.handlers.TeamSearchModalHandler
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class RequestCommand (
    executorSearchModalHandler: ExecutorSearchModalHandler,
    teamSearchModalHandler: TeamSearchModalHandler,
    teamRecruitmentModalHandler: TeamRecruitmentModalHandler
): ICommandHandler {
    companion object {
        private const val NAME = "request"
        private const val DESCRIPTION = "Create a new request"
        private const val SERVICE_TYPE_OPTION_NAME = "type"
        private const val REQUEST_TYPE_OPTION_DESCRIPTION = "Тип запроса"
        private const val EXECUTOR_SEARCH_LABEL = "Поиск исполнителя"
        private const val TEAM_RECRUITMENT_LABEL = "Набор команды"
        private const val TEAM_SEARCH_LABEL = "Поиск команды"
    }

    // <request type, <label, modal handler>>
    private val requestTypesData: Map<RequestType, Pair<String, IModalHandler>> = mutableMapOf(
        RequestType.EXECUTOR_SEARCH to Pair(EXECUTOR_SEARCH_LABEL, executorSearchModalHandler),
        RequestType.TEAM_RECRUITMENT to Pair(TEAM_RECRUITMENT_LABEL, teamRecruitmentModalHandler),
        RequestType.TEAM_SEARCH to Pair(TEAM_SEARCH_LABEL, teamSearchModalHandler),
    )

    /*
     * TODO: create a sсheduler that deletes messages in the request channels a month after they are published
     * TODO: add a limit on the number of requests created per user?
     */
    override fun handle(event: ApplicationCommandInteractionEvent): Mono<Void> {
        val exception = IllegalArgumentException("Invalid request type")
        val requestType = event.interaction.commandInteraction
                .flatMap { it.getOption(SERVICE_TYPE_OPTION_NAME) }
                .flatMap { it.value }
                .map { RequestType.findByProperty(it.asString()) }
                .orElseThrow { exception }

        val requestTypeData = requestTypesData[requestType] ?: throw exception

        return event.presentModal(requestTypeData.second.create())
    }

    override fun register(): ApplicationCommandRequest {
        ApplicationCommandPermissionsData.builder()
                .id(1)
                .type(ApplicationCommandPermission.Type.ROLE.value)
                .permission(true)
                .build()

        val serviceType = ApplicationCommandOptionData.builder()
                .name(SERVICE_TYPE_OPTION_NAME)
                .description(REQUEST_TYPE_OPTION_DESCRIPTION)
                .type(ApplicationCommandOption.Type.STRING.value)
                .required(true)

        this.requestTypesData.forEach { (requestType, data) ->
            val choice = ApplicationCommandOptionChoiceData.builder()
                    .name(data.first)
                    .value(requestType)
                    .build()

            serviceType.addChoice(choice)
        }

        return ApplicationCommandRequest.builder()
                .name(NAME)
                .description(DESCRIPTION)
                .addOption(serviceType.build())
                .build()
    }

    override fun name(): String {
        return NAME
    }
}