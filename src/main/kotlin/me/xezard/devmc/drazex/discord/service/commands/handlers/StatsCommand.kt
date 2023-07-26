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
import discord4j.core.spec.EmbedCreateSpec
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec
import discord4j.discordjson.json.ApplicationCommandRequest
import discord4j.rest.util.Permission
import me.xezard.devmc.drazex.discord.config.DiscordConfiguration
import me.xezard.devmc.drazex.discord.config.properties.RolesProperties
import me.xezard.devmc.drazex.discord.service.app.AppService
import me.xezard.devmc.drazex.discord.service.app.AppService.Companion.AVAILABLE_MEMORY_REPLACE_PLACEHOLDER
import me.xezard.devmc.drazex.discord.service.app.AppService.Companion.MAXIMUM_MEMORY_REPLACE_PLACEHOLDER
import me.xezard.devmc.drazex.discord.service.app.AppService.Companion.UPTIME_REPLACE_PLACEHOLDER
import me.xezard.devmc.drazex.discord.service.app.AppService.Companion.USED_MEMORY_REPLACE_PLACEHOLDER
import me.xezard.devmc.drazex.discord.service.commands.CommandHandler
import me.xezard.devmc.drazex.discord.service.messages.MessagesService
import me.xezard.devmc.drazex.discord.service.roles.RolesService
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class StatsCommand (
    private val appService: AppService,
    private val messagesService: MessagesService,
    private val rolesService: RolesService,
    private val discordConfiguration: DiscordConfiguration,
    private val rolesProperties: RolesProperties
): CommandHandler {
    companion object {
        private val MEMORY_INFO_MESSAGE = """
        Используется: %s МБ
        Доступная: %s МБ
        Всего: %s МБ
        """.trimIndent()

        private const val EMBED_TITLE = "Статистика"
        private const val EMBED_UPTIME_FIELD = "Аптайм"
        private const val EMBED_MEMORY_FIELD = "Память"

        private val COMMAND_DEFAULT_PERMISSION = Permission.ADMINISTRATOR.value.toString()

        private const val COMMAND_DESCRIPTION = "Display the overall statistics of the bot"
        private const val COMMAND = "stats"
    }

    override val name
        get() = COMMAND

    override fun handle(event: ApplicationCommandInteractionEvent): Mono<Void> {
        return Mono.justOrEmpty(event.interaction.member)
            .filterWhen { this.rolesService.hasRole(it, this.rolesProperties.admin) }
            .flatMap { this.createStatsEmbed(event) }
    }

    private fun createStatsEmbed(event: ApplicationCommandInteractionEvent): Mono<Void> {
        val replaces = appService.replaces.invoke()

        val uptime = replaces[UPTIME_REPLACE_PLACEHOLDER].toString()
        val memoryInfo = String.format(MEMORY_INFO_MESSAGE,
            replaces[USED_MEMORY_REPLACE_PLACEHOLDER],
            replaces[AVAILABLE_MEMORY_REPLACE_PLACEHOLDER],
            replaces[MAXIMUM_MEMORY_REPLACE_PLACEHOLDER]
        )

        val embed = EmbedCreateSpec.builder()
            .title(EMBED_TITLE)
            .color(this.messagesService.getColorFromString(this.discordConfiguration.messagesColor))
            .addField(EMBED_UPTIME_FIELD, uptime, false)
            .addField(EMBED_MEMORY_FIELD, memoryInfo, false)
            .build()

        return event.reply(InteractionApplicationCommandCallbackSpec.builder()
            .addEmbed(embed)
            .build()
            .withEphemeral(true))
    }

    override fun register(): ApplicationCommandRequest {
        return ApplicationCommandRequest.builder()
                .name(COMMAND)
                .description(COMMAND_DESCRIPTION)
                .defaultMemberPermissions(COMMAND_DEFAULT_PERMISSION)
                .build()
    }
}