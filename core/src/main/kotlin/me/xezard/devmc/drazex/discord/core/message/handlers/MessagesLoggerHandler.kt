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
import me.xezard.devmc.drazex.discord.core.message.MessageHandler
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.logging.Logger

@Component
class MessagesLoggerHandler (
    private val discordService: DiscordService
) : MessageHandler {
    companion object {
        private val LOGGER: Logger = Logger.getLogger("[MLH]")

        private const val CHANNEL_ID_REPLACE_PLACEHOLDER = "{channel-id}"
        private const val AUTHOR_REPLACE_PLACEHOLDER = "{author}"
        private const val CONTENT_REPLACE_PLACEHOLDER = "{content}"

        private const val LOG_FORMAT = "(channel id: $CHANNEL_ID_REPLACE_PLACEHOLDER) " +
                "$AUTHOR_REPLACE_PLACEHOLDER $CONTENT_REPLACE_PLACEHOLDER"
    }

    override fun handle(event: MessageCreateEvent): Mono<Void> {
        val message = event.message

        return Mono.justOrEmpty(message.author)
            .map { it.username }
            .flatMap { Mono.fromRunnable {
                LOGGER.info(LOG_FORMAT
                    .replace(CHANNEL_ID_REPLACE_PLACEHOLDER, message.channelId.asString())
                    .replace(AUTHOR_REPLACE_PLACEHOLDER, it)
                    .replace(CONTENT_REPLACE_PLACEHOLDER, message.content))
            }}
    }

    override fun handled(message: Message) =
        !this.discordService.authorIsBot(message)
}