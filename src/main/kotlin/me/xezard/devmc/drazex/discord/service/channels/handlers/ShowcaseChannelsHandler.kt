package me.xezard.devmc.drazex.discord.service.channels.handlers

import discord4j.core.`object`.entity.Message
import discord4j.core.`object`.reaction.ReactionEmoji
import me.xezard.devmc.drazex.discord.config.properties.ChannelsProperties
import me.xezard.devmc.drazex.discord.service.channels.IChannelHandler
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class ShowcaseChannelsHandler(
    private val channelsProperties: ChannelsProperties
): IChannelHandler {
    override fun handle(message: Message): Mono<Void> {
        if (message.author.isEmpty) {
            return Mono.empty()
        }

        return message.addReaction(ReactionEmoji.unicode("👍"))
                .then(message.addReaction(ReactionEmoji.unicode("👎")))
                .then(message.addReaction(ReactionEmoji.unicode("🍪")))
    }

    override fun getHandledChannelIds(): List<String> {
        return channelsProperties.showcase
    }
}