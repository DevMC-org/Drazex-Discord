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
package me.xezard.devmc.drazex.discord.core.commands.handlers

import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec
import me.xezard.devmc.drazex.discord.core.app.AppService
import me.xezard.devmc.drazex.discord.core.app.DiscordService
import me.xezard.devmc.drazex.discord.core.commands.AbstractCommandHandler
import me.xezard.devmc.drazex.discord.core.commands.CommandsService
import me.xezard.devmc.drazex.discord.core.config.discord.commands.CommandsProperties
import me.xezard.devmc.drazex.discord.core.config.discord.messages.MessagesProperties
import me.xezard.devmc.drazex.discord.core.config.discord.roles.RolesProperties
import me.xezard.devmc.drazex.discord.core.message.service.MessageService
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class StatsCommand (
    commandsService: CommandsService,
    private val appService: AppService,
    private val messageService: MessageService,
    private val discordService: DiscordService,
    commandsProperties: CommandsProperties,
    private val messagesProperties: MessagesProperties,
    private val rolesProperties: RolesProperties,
): AbstractCommandHandler(commandsService, commandsProperties.stats) {
    override fun handle(event: ApplicationCommandInteractionEvent): Mono<Void> =
        Mono.justOrEmpty(event.interaction.member)
            .filterWhen { this.discordService.hasRole(it, this.rolesProperties.admin) }
            .flatMap { this.createStatsEmbed(event) }

    private fun createStatsEmbed(event: ApplicationCommandInteractionEvent): Mono<Void> {
        val replaces = this.appService.replaces.invoke()
        val message = this.messageService.embedFrom(this.messagesProperties.stats, replaces) ?: return Mono.empty()

        return event.reply(InteractionApplicationCommandCallbackSpec.builder()
            .addEmbed(message)
            .build()
            .withEphemeral(true))
    }
}