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
package me.xezard.devmc.drazex.discord.core.message.handlers

import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.`object`.entity.Message
import me.xezard.devmc.drazex.discord.core.app.DiscordService
import me.xezard.devmc.drazex.discord.core.channels.ChannelsHandler
import me.xezard.devmc.drazex.discord.core.message.MessageHandler
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class ChannelMessageHandler (
    private val discordService: DiscordService,
    private val channelsHandler: ChannelsHandler
) : MessageHandler {
    override fun handle(event: MessageCreateEvent): Mono<Void> =
        this.channelsHandler.handle(event.message)

    override fun handled(message: Message) =
        !this.discordService.authorIsBot(message)
}