package me.xezard.devmc.drazex.discord.events.handlers

import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.spec.EmbedCreateSpec
import discord4j.rest.util.Color
import me.xezard.devmc.drazex.discord.events.IEventHandler
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class MessageCreateEventHandler: IEventHandler<MessageCreateEvent> {
    override fun handle(event: MessageCreateEvent): Mono<Void> {
        val embed: EmbedCreateSpec = EmbedCreateSpec.builder().color(Color.GREEN).title("Привет")
                .description("Статус: Онлайн")
                .build()

        return event.message.channel.map { channel ->
            channel.createMessage(embed)
        }.then()
    }

    override fun getEvent(): Class<MessageCreateEvent> {
        return MessageCreateEvent::class.java
    }
}