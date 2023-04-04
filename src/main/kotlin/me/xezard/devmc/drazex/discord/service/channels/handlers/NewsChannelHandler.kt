package me.xezard.devmc.drazex.discord.service.channels.handlers

import discord4j.common.util.Snowflake
import discord4j.core.`object`.entity.Message
import discord4j.core.spec.EmbedCreateSpec
import discord4j.discordjson.json.MessageCreateRequest
import discord4j.discordjson.json.MessageData
import discord4j.rest.util.Color
import me.xezard.devmc.drazex.discord.config.DiscordConfiguration
import me.xezard.devmc.drazex.discord.config.properties.NewsChannelsProperties
import me.xezard.devmc.drazex.discord.service.channels.IChannelHandler
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class NewsChannelHandler (
        private val discordConfiguration: DiscordConfiguration,
        private val newsChannelsProperties: NewsChannelsProperties
): IChannelHandler {
    companion object {
        val DISCORD_EMOJI_PATTERN = Regex("<:\\w+:\\d+>")

        const val DISCORD_AVATARS_URL = "https://cdn.discordapp.com/avatars/{user_id}/{avatar}.png"
        const val DISCORD_CHANNELS_URL = "https://discordapp.com/channels/{id}"
        const val CHANNEL_NAME_PATTERN = " #\\w+"
    }

    override fun handle(message: Message): Mono<Void> {
        val messageData: MessageData = message.data
        val author = messageData.author()
        val avatar = DISCORD_AVATARS_URL
                .replace("{user_id}", author.id().asString())
                .replace("{avatar}", author.avatar().orElse("1"))
        val guildId = message.messageReference.flatMap { ref -> ref.guildId }
                .orElse(Snowflake.of(23423))
                .asString()
        val channelUrl = DISCORD_CHANNELS_URL.replace("{id}", guildId)
        val embedBuilder: EmbedCreateSpec.Builder = EmbedCreateSpec.builder()
                .author(author.username().replace(CHANNEL_NAME_PATTERN.toRegex(), ""),
                        channelUrl, avatar)
                .color(Color.of(33, 247, 4))
                .description(message.content.replace(DISCORD_EMOJI_PATTERN, ""))
                .thumbnail(avatar)
                .footer("• Лента новостей сообщества", "")

        messageData.attachments().firstOrNull()?.url()?.let { embedBuilder.image(it) }

        val consumerId = Snowflake.of(this.discordConfiguration.newsConsumerChannelId)
        val messageRequest = MessageCreateRequest.builder()
                .addEmbed(embedBuilder.build().asRequest())

        messageRequest.addAllEmbeds(message.embeds.map { embed -> embed.data })

        return message.guild.flatMap { guild -> guild.getChannelById(consumerId) }
                .flatMap { channel -> channel.restChannel.createMessage(messageRequest.build()) }
                .then()
    }

    override fun getHandledChannelIds(): List<String> {
        return this.newsChannelsProperties.publishers
    }
}