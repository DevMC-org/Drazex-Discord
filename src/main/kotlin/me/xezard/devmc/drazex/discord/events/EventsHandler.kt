package me.xezard.devmc.drazex.discord.events

import discord4j.core.GatewayDiscordClient
import discord4j.core.event.domain.Event
import discord4j.core.event.domain.lifecycle.ReadyEvent
import discord4j.core.`object`.entity.User
import org.springframework.beans.factory.annotation.Autowired
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class EventsHandler {
    @Autowired
    private val handlers = ArrayList<IEventHandler<Event>> ()

    fun registerAll(gateway: GatewayDiscordClient): Mono<Void> {
        return Flux.fromIterable(this.handlers).map { handler ->
            gateway.on(handler.getClass()) { event ->
                handler.handle(event).then()
            }
        }.then()
    }
}