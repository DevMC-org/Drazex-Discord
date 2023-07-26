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

import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.`object`.entity.Message
import me.xezard.devmc.drazex.discord.service.channels.ChannelsHandler
import me.xezard.devmc.drazex.discord.service.events.EventHandler
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

@Component
class MessageCreateHandler (
    private val channelsHandler: ChannelsHandler
): EventHandler<MessageCreateEvent> {
    companion object {
        private val LOGGER: Logger = Logger.getLogger("[MCH]")

        private const val CHANNEL_ID_REPLACE_PLACEHOLDER = "{channel-id}"
        private const val AUTHOR_REPLACE_PLACEHOLDER = "{author}"
        private const val CONTENT_REPLACE_PLACEHOLDER = "{content}"

        private const val LOG_ERROR_MESSAGE = "An unknown error occurred: "

        private const val LOG_FORMAT = "(channel id: $CHANNEL_ID_REPLACE_PLACEHOLDER) " +
                "$AUTHOR_REPLACE_PLACEHOLDER $CONTENT_REPLACE_PLACEHOLDER"
    }

    override val event
        get() = MessageCreateEvent::class.java

    override fun handle(event: MessageCreateEvent): Mono<Void> {
        return Mono.justOrEmpty(event.message)
                   .flatMap(::processMessage)
                   .then()
                   .onErrorResume(::handleError)
    }

    private fun processMessage(message: Message): Mono<Void> {
        if (this.authorIsBot(message)) {
            return Mono.empty()
        }

        val author = message.author.map { "${it.username}#${it.discriminator}" }
                .orElse("[?]")

        LOGGER.info(LOG_FORMAT
            .replace(CHANNEL_ID_REPLACE_PLACEHOLDER, message.channelId.asString())
            .replace(AUTHOR_REPLACE_PLACEHOLDER, author)
            .replace(CONTENT_REPLACE_PLACEHOLDER, message.content))

        return channelsHandler.handle(message)
    }

    private fun authorIsBot(message: Message) =
        message.author.map { it.isBot }.orElse(false)

    private fun handleError(exception: Throwable): Mono<Void> = Mono.fromRunnable {
        LOGGER.log(Level.WARNING,
            "$LOG_ERROR_MESSAGE ${Objects.requireNonNullElse(exception.message, "")}", exception)
    }
}