package me.xezard.devmc.drazex.discord.events

import discord4j.core.event.domain.Event
import reactor.core.publisher.Mono

interface IEventHandler<out T : Event> {
    fun handle(event: @UnsafeVariance T): Mono<Void>

    fun getEvent(): Class<out T>
}