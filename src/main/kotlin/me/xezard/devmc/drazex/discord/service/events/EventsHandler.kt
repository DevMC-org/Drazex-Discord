package me.xezard.devmc.drazex.discord.service.events

import discord4j.core.GatewayDiscordClient
import discord4j.core.event.domain.Event
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.logging.Level
import java.util.logging.Logger

@Service
class EventsHandler {
    @Autowired
    private lateinit var handlers: List<IEventHandler<Event>>

    fun registerAllHandlers(gateway: GatewayDiscordClient): Mono<Void> {
        return Flux.fromIterable(this.handlers).map { handler ->
            gateway.on(handler.getEvent()) { event ->
                handler.handle(event)
            }.onErrorResume { error ->
                Logger.getLogger("[EH]").log(Level.WARNING, "[EH] Error while handling event", error)
                Mono.empty()
            }
        }.then()
    }
}