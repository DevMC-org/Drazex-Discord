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
package me.xezard.devmc.drazex.discord.service.events.handlers

import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent
import me.xezard.devmc.drazex.discord.service.commands.CommandsHandler
import me.xezard.devmc.drazex.discord.service.events.EventHandler
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class ApplicationCommandInteractionHandler (
    private val commandsHandler: CommandsHandler
): EventHandler<ApplicationCommandInteractionEvent> {
    override val event
        get() = ApplicationCommandInteractionEvent::class.java

    override fun handle(event: ApplicationCommandInteractionEvent): Mono<Void> {
        return this.commandsHandler.findHandlerByCommandName(event.commandName)?.handle(event) ?: Mono.empty()
    }
}