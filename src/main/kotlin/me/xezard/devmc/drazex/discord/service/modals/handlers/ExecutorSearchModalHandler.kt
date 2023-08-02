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
package me.xezard.devmc.drazex.discord.service.modals.handlers

import discord4j.core.event.domain.interaction.ModalSubmitInteractionEvent
import me.xezard.devmc.drazex.discord.config.discord.channels.ChannelsProperties
import me.xezard.devmc.drazex.discord.config.discord.messages.MessagesProperties
import me.xezard.devmc.drazex.discord.config.discord.modals.ModalsProperties
import me.xezard.devmc.drazex.discord.service.messages.MessagesService
import me.xezard.devmc.drazex.discord.service.modals.ModalsService
import org.springframework.stereotype.Component

@Component
class ExecutorSearchModalHandler (
    modalsService: ModalsService,
    messagesService: MessagesService,
    messagesProperties: MessagesProperties,
    modalsProperties: ModalsProperties,
    private val channelsProperties: ChannelsProperties
): RequestModalHandler(
    modalsService,
    messagesService,
    messagesProperties,
    modalsProperties.executorSearch
) {
    companion object {
        private const val EMBED_TITLE = "Запрос:"
    }

    override fun handle(event: ModalSubmitInteractionEvent) =
        this.handle(event, this.channelsProperties.requests.development, EMBED_TITLE)
}