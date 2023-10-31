package me.xezard.devmc.drazex.discord.core.scheduler

import discord4j.common.util.Snowflake
import discord4j.core.spec.EmbedCreateSpec
import discord4j.rest.entity.RestMessage
import me.xezard.devmc.drazex.discord.core.DrazexBot
import me.xezard.devmc.drazex.discord.core.app.AppService
import me.xezard.devmc.drazex.discord.core.config.discord.channels.ChannelsProperties
import me.xezard.devmc.drazex.discord.core.config.discord.messages.MessagesProperties
import me.xezard.devmc.drazex.discord.core.message.service.MessageService
import org.springframework.scheduling.annotation.Scheduled
import reactor.core.publisher.Mono
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

class StatsScheduler (
    private val bot: DrazexBot,
    private val appService: AppService,
    private val messagesProperties: MessagesProperties,
    private val channelsProperties: ChannelsProperties,
    private val messageService: MessageService
) {

    companion object {
        private val FORMATTER = DateTimeFormatter.ISO_INSTANT
    }

    private var messageId = Snowflake.of("null")
    private var channelId = Snowflake.of(channelsProperties.publishers.statPublisher.channel)

    @Scheduled(fixedDelay = 5, timeUnit = TimeUnit.MINUTES)
    fun checkStatMessage() {
        if (messageId.toString() == "null") createStatMessage().subscribe()
        else updateStatMessage(this.bot.discord.getMessageById(channelId, messageId)).subscribe()
    }

    private fun createStatMessage(): Mono<Void>  {
        val newMessage = messageCreator()
        val channel = this.bot.discord.getChannelById(channelId)
        channel.createMessage(newMessage?.asRequest())
        return Mono.empty()
    }


    private fun updateStatMessage(message: RestMessage): Mono<Void> {
        val newMessage = messageCreator()
        message.data.map {
            it.embeds().removeAll { true }
            it.embeds().add(newMessage?.asRequest())
        }
        messageId = message.id
        return Mono.empty()
    }

    private fun messageCreator(): EmbedCreateSpec? {
        val replaces = this.appService.replaces.invoke()
        return this.messageService.embedFrom(this.messagesProperties.stats, replaces)

    }
}