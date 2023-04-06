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
    private val handlers: List<IEventHandler<Event>>
) {
    fun registerAllHandlers(gateway: GatewayDiscordClient): Mono<Void> {
        return Flux.fromIterable(this.handlers).flatMap { handler ->
            gateway.on(handler.getEvent()) { event ->
                handler.handle(event)
            }.onErrorResume { error -> Mono.fromRunnable {
                Logger.getLogger("[EH]").log(Level.WARNING, "[EH] Error while handling event", error)
            }}
        }.then()
    }
}