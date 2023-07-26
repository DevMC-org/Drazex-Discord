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
package me.xezard.devmc.drazex.discord.service.events

import discord4j.core.GatewayDiscordClient
import discord4j.core.event.domain.Event
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.logging.Level
import java.util.logging.Logger

@Service
class EventsHandler (
    private val handlers: List<EventHandler<Event>>
) {
    companion object {
        private const val LOGGER_PREFIX = "[EH]"

        private const val EVENT_HANDLING_ERROR_MESSAGE = "Error while handling event"

        private val LOGGER: Logger = Logger.getLogger(LOGGER_PREFIX)
    }

    fun registerAllHandlers(gateway: GatewayDiscordClient): Mono<Void> =
        Flux.fromIterable(this.handlers).flatMap { handler ->
            gateway.on(handler.event) { handler.handle(it) }
                .onErrorResume { Mono.fromRunnable { LOGGER.log(Level.WARNING, EVENT_HANDLING_ERROR_MESSAGE, it) }}
        }.then()
}