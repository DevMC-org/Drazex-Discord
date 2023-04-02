package me.xezard.devmc.drazex.discord.service.events.handlers

import discord4j.core.event.domain.lifecycle.ReadyEvent
import discord4j.core.`object`.entity.User
import me.xezard.devmc.drazex.discord.service.events.IEventHandler
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.logging.Logger

@Component
class ReadyEventHandler: IEventHandler<ReadyEvent> {
    override fun handle(event: ReadyEvent): Mono<Void> {
        return Mono.fromRunnable {
            val self: User = event.self

            Logger.getLogger("[REH]").info("Logged in as ${self.username}#${self.discriminator}")
        }
    }

    override fun getEvent(): Class<ReadyEvent> {
        return ReadyEvent::class.java
    }
}