package me.xezard.devmc.drazex.discord.events

import discord4j.core.DiscordClient
import discord4j.core.GatewayDiscordClient
import discord4j.core.event.domain.Event
import discord4j.core.event.domain.lifecycle.ReadyEvent
import discord4j.core.`object`.entity.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class EventsHandler(
    var handlers: ArrayList<IEventHandler<Event>>

) {

    fun registerAll(gateway: GatewayDiscordClient): Mono<Void> {
        return Flux.fromIterable(this.handlers).map { handler ->
            gateway.on(handler.getEventClass()) { event ->
                handler.handle(event).then()
            }
        }.then()
    }
}