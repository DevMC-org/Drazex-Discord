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
package me.xezard.devmc.drazex.discord.core.commands

import discord4j.core.DiscordClient
import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent
import me.xezard.devmc.drazex.discord.core.app.Handler
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class CommandsHandler (
    private val handlers: List<CommandHandler>
) : Handler<ApplicationCommandInteractionEvent> {
    fun registerAllHandlers(discordClient: DiscordClient): Mono<Void> =
        discordClient.applicationId.flatMapMany { appId -> Flux.fromIterable(this.handlers).flatMap {
            discordClient.applicationService.createGlobalApplicationCommand(appId, it.register())
        }}.then()

    override fun handle(value: ApplicationCommandInteractionEvent): Mono<Void> =
        this.findHandlerByCommandName(value.commandName)?.handle(value) ?: Mono.empty()

    fun findHandlerByCommandName(name: String) =
        this.handlers.find { it.name == name }
}