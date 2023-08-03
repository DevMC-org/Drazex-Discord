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
package me.xezard.devmc.drazex.discord.core.buttons

import discord4j.core.event.domain.interaction.ButtonInteractionEvent
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ButtonsHandler (
    private val handlers: List<ButtonHandler>
) {
    fun handle(event: ButtonInteractionEvent): Mono<Void> =
        Mono.justOrEmpty(event.interaction.data.data().toOptional())
            .flatMap { Mono.justOrEmpty(it.customId().toOptional()) }
            .flatMap { this.findButtonById(it)?.handle(event, it) }

    fun findButtonById(id: String) =
        this.handlers.find { it.tracks(id) }
}