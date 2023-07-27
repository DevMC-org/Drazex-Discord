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
import me.xezard.devmc.drazex.discord.config.DiscordConfiguration
import me.xezard.devmc.drazex.discord.config.commands.CommandsConfiguration
import me.xezard.devmc.drazex.discord.config.roles.properties.RolesProperties
import me.xezard.devmc.drazex.discord.service.app.AppService
import me.xezard.devmc.drazex.discord.service.app.AppService.Companion.AVAILABLE_MEMORY_REPLACE_PLACEHOLDER
import me.xezard.devmc.drazex.discord.service.app.AppService.Companion.MAXIMUM_MEMORY_REPLACE_PLACEHOLDER
import me.xezard.devmc.drazex.discord.service.app.AppService.Companion.UPTIME_REPLACE_PLACEHOLDER
import me.xezard.devmc.drazex.discord.service.app.AppService.Companion.USED_MEMORY_REPLACE_PLACEHOLDER
import me.xezard.devmc.drazex.discord.service.commands.AbstractCommandHandler
import me.xezard.devmc.drazex.discord.service.commands.CommandsService
import me.xezard.devmc.drazex.discord.service.messages.MessagesService
import me.xezard.devmc.drazex.discord.service.roles.RolesService
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class StatsCommand (
    commandsService: CommandsService,
    private val appService: AppService,
    private val messagesService: MessagesService,
    private val rolesService: RolesService,
    private val discordConfiguration: DiscordConfiguration,
    commandsConfiguration: CommandsConfiguration,
    private val rolesProperties: RolesProperties
): AbstractCommandHandler(commandsService, commandsConfiguration.commands["stats"]!!) {
    companion object {
        private val MEMORY_INFO_MESSAGE = """
        Используется: %s МБ
        Доступная: %s МБ
        Всего: %s МБ
        """.trimIndent()

        private const val EMBED_TITLE = "Статистика"
        private const val EMBED_UPTIME_FIELD = "Аптайм"
        private const val EMBED_MEMORY_FIELD = "Память"
    }

    override fun handle(event: ApplicationCommandInteractionEvent) =
        Mono.justOrEmpty(event.interaction.member)
            .filterWhen { this.rolesService.hasRole(it, this.rolesProperties.admin) }
            .flatMap { this.createStatsEmbed(event) }

    private fun createStatsEmbed(event: ApplicationCommandInteractionEvent): Mono<Void> {
        val replaces = this.appService.replaces.invoke()

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
}