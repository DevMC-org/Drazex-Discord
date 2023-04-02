package me.xezard.devmc.drazex.discord.service.channels

import discord4j.core.`object`.entity.Message
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class ChannelsHandler (
        var handlers: List<IChannelHandler>
) {
    fun handle(message: Message): Mono<Void> {
        return Flux.fromIterable(this.findHandlersByChannelId(message.channelId.asString()))
                .flatMap { handler -> handler.handle(message) }
                .then()
    }

    private fun findHandlersByChannelId(channelId: String): List<IChannelHandler> {
        return this.handlers.filter { handler -> handler.getHandledChannelIds().contains(channelId) }
    }
}