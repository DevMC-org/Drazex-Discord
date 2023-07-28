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
import me.xezard.devmc.drazex.discord.config.commands.CommandsConfiguration
import me.xezard.devmc.drazex.discord.domain.model.request.RequestType
import me.xezard.devmc.drazex.discord.service.commands.AbstractCommandHandler
import me.xezard.devmc.drazex.discord.service.commands.CommandsService
import me.xezard.devmc.drazex.discord.service.modals.handlers.ExecutorSearchModalHandler
import me.xezard.devmc.drazex.discord.service.modals.handlers.TeamRecruitmentModalHandler
import me.xezard.devmc.drazex.discord.service.modals.handlers.TeamSearchModalHandler
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class RequestCommand (
    private val commandsService: CommandsService,
    private val executorSearchModalHandler: ExecutorSearchModalHandler,
    private val teamSearchModalHandler: TeamSearchModalHandler,
    private val teamRecruitmentModalHandler: TeamRecruitmentModalHandler,
    commandsConfiguration: CommandsConfiguration
): AbstractCommandHandler(commandsService, commandsConfiguration.commands["request"]!!) {
    companion object {
        private const val SERVICE_TYPE_OPTION_NAME = "type"
    }

    // <request type, modal handler>
    private val requestTypesData = mapOf(
        RequestType.EXECUTOR_SEARCH to this.executorSearchModalHandler,
        RequestType.TEAM_RECRUITMENT to this.teamRecruitmentModalHandler,
        RequestType.TEAM_SEARCH to this.teamSearchModalHandler
    )

    // TODO: add a limit on the number of requests created per user?
    override fun handle(event: ApplicationCommandInteractionEvent) =
        Mono.justOrEmpty(this.commandsService.extractValue(event, SERVICE_TYPE_OPTION_NAME))
            .flatMap { Mono.justOrEmpty(RequestType.findByProperty(it)) }
            .flatMap { Mono.justOrEmpty(this.requestTypesData[it]?.create()) }
            .flatMap { event.presentModal(it) }
}