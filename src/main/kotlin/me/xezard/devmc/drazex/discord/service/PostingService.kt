package me.xezard.devmc.drazex.discord.service

import discord4j.common.util.Snowflake
import discord4j.core.spec.EmbedCreateSpec
import me.xezard.devmc.drazex.discord.DrazexBot
import me.xezard.devmc.drazex.discord.config.DiscordConfiguration
import me.xezard.devmc.drazex.discord.config.MessagesConfiguration
import me.xezard.devmc.drazex.discord.domain.model.post.DiscordPost
import me.xezard.devmc.drazex.discord.domain.model.post.DiscordPostType
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.awt.Color

@Service
class PostingService (
    var bot: DrazexBot,

    val discordConfiguration: DiscordConfiguration,
    val messagesConfiguration: MessagesConfiguration
) {
    // <post type, post template>
    private val postTemplates = mutableMapOf<DiscordPostType, Array<String>?>().apply {
        this[DiscordPostType.RESOURCE] = messagesConfiguration.newResourcePostTemplate
        this[DiscordPostType.RESOURCE_VERSION] = messagesConfiguration.newResourceVersionPostTemplate
        this[DiscordPostType.ARTICLE] = messagesConfiguration.newArticlePostTemplate
    }

    fun generatePost(post: DiscordPost): Mono<Void> {
        val message = post.toMessage(this.postTemplates[post.type]!!)

        return this.publish(message, post.imageUrl)
    }

    private fun publish(message: String, imageUrl: String): Mono<Void> {
        return this.bot.discord.getChannelById(Snowflake.of(this.discordConfiguration.newsConsumerChannelId))
                .createMessage(EmbedCreateSpec.builder()
                        .title("DevMC")
                        .url("https://devmc.org")
                        .color(discord4j.rest.util.Color.of(Color.decode(this.discordConfiguration.messagesColor).rgb))
                        .description(message)
                        .image(imageUrl)
                        .thumbnail(this.discordConfiguration.thumbnailUrl)
                        .build()
                        .asRequest())
                .then()
    }
}