package me.xezard.devmc.drazex.discord.events.handlers

import discord4j.core.event.domain.lifecycle.ReadyEvent
import discord4j.core.`object`.entity.User
import me.xezard.devmc.drazex.discord.events.IEventHandler
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class ReadyEventHandler: IEventHandler<ReadyEvent> {
    override fun handle(event: ReadyEvent): Mono<Void> {
        return Mono.fromRunnable {
            val self: User = event.self

            System.out.printf(
                    "Logged in as %s#%s%n",
                    self.username,
                    self.discriminator
            )
        }
    }

    override fun getEvent(): Class<ReadyEvent> {
        return ReadyEvent::class.java
    }
}