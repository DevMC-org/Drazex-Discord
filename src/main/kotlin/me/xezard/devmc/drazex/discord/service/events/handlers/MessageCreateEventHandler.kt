package me.xezard.devmc.drazex.discord.service.events.handlers

import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.`object`.entity.Message
import me.xezard.devmc.drazex.discord.service.channels.ChannelsHandler
import me.xezard.devmc.drazex.discord.service.events.IEventHandler
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

@Component
class MessageCreateEventHandler (
        private var channelsHandler: ChannelsHandler
): IEventHandler<MessageCreateEvent> {
    companion object {
        val LOGGER: Logger = Logger.getLogger("[MCE]")
    }

    override fun handle(event: MessageCreateEvent): Mono<Void> {
        return Mono.justOrEmpty(event.message)
                   .flatMap(::processMessage)
                   .then()
                   .onErrorResume(::handleError)
    }

    private fun processMessage(message: Message): Mono<Void> {
        val author = message.author.map { "${it.username}#${it.discriminator}" }.orElse("[unknown user]")
        val bot = message.author.map { user -> user.isBot }.orElse(false)
        val command = message.content.startsWith("/") && !bot

        LOGGER.info("(command: ${command}, channel id: ${message.channelId}) $author ${message.content}")

        if (command) {
            // handle command here ???
            // commandsManager.handle(message)
            return Mono.empty()
        }

        return channelsHandler.handle(message)
    }

    private fun handleError(exception: Throwable): Mono<Void> {
        return Mono.fromRunnable {
            LOGGER.log(Level.WARNING, "An unknown error occurred: " +
                    Objects.requireNonNullElse(exception.message, ""), exception)
        }
    }

    override fun getEvent(): Class<MessageCreateEvent> {
        return MessageCreateEvent::class.java
    }
}