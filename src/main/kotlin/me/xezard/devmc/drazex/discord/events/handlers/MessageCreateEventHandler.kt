package me.xezard.devmc.drazex.discord.events.handlers

import discord4j.core.DiscordClient
import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.spec.EmbedCreateSpec
import discord4j.rest.util.Color
import me.xezard.devmc.drazex.discord.events.IEventHandler
import org.springframework.beans.factory.annotation.Autowired
import reactor.core.publisher.Mono

class MessageCreateEventHandler (
        @Autowired
        private var discord: DiscordClient
): IEventHandler<MessageCreateEvent> {
    override fun handle(event: MessageCreateEvent): Mono<Void> {
        val embed: EmbedCreateSpec = EmbedCreateSpec.builder().color(Color.GREEN).title("Привет")
                .description("Статус: Онлайн")
                .build()

        return this.discord.getChannelById(event.message.channelId)
                .createMessage(embed.asRequest())
                .then()
    }

    override fun getClass(): Class<MessageCreateEvent> {
        return MessageCreateEvent::class.java
    }
}