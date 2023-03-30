package me.xezard.devmc.drazex.discord.events

import discord4j.core.event.domain.Event
import reactor.core.publisher.Mono

interface IEventHandler<T : Event> {
    fun handle(event: T): Mono<Void>

    fun getEventClass(): Class<T>;
}