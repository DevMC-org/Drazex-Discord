package me.xezard.devmc.drazex.discord.service.channels

import org.springframework.beans.factory.annotation.Autowired

class ChannelsHandler {
    @Autowired
    private lateinit var handlers: List<IChannelHandler>

    /*fun handle(message: Message): Mono<Void> {
        return Mono.justOrEmpty(this.getHandlerByChannelId(message.channelId.asString()))
                .flatMap { handler -> handler.handle(message) }
    }

    private fun getHandlerByChannelId(channelId: String): Optional<IChannelHandler> {
        return this.handlers.stream()
                .filter { handler -> handler.getChannelIds().contains(channelId) }
                .findFirst()
    }*/
}