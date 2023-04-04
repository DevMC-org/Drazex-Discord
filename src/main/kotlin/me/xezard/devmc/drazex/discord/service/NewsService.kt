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
class NewsService (
    private val bot: DrazexBot,

    private val discordConfiguration: DiscordConfiguration,
    private val messagesConfiguration: MessagesConfiguration
) {
    // <post type, post template>
    private val postTemplates = mutableMapOf<DiscordPostType, Array<String>?>().apply {
        this[DiscordPostType.RESOURCE] = messagesConfiguration.newResourcePostTemplate
        this[DiscordPostType.RESOURCE_VERSION] = messagesConfiguration.newResourceVersionPostTemplate
        this[DiscordPostType.ARTICLE] = messagesConfiguration.newArticlePostTemplate
    }

    fun publishNews(post: DiscordPost): Mono<Void> {
        return this.generatePost(this.assembleMessage(post), post.imageUrl)
    }

    private fun generatePost(message: String, imageUrl: String): Mono<Void> {
        val embed = EmbedCreateSpec.builder()
                .title("DevMC")
                .url(this.discordConfiguration.baseUrl)
                .color(discord4j.rest.util.Color.of(Color.decode(this.discordConfiguration.messagesColor).rgb))
                .description(message)
                .image(imageUrl)
                .thumbnail(this.discordConfiguration.thumbnailUrl)
                .build()
                .asRequest()

        return this.bot.discord.getChannelById(Snowflake.of(this.discordConfiguration.newsConsumerChannelId))
                .createMessage(embed)
                .then()
    }

    private fun assembleMessage(post: DiscordPost): String {
        val template = this.postTemplates[post.type]!!
        val baseUrl = this.discordConfiguration.baseUrl
        val replaces = post.replaces

        replaces["{url}"] = "${baseUrl}/${replaces["{url}"]}"

        return template.joinToString(separator = "\n") { message ->
            replaces.entries.fold(message) { entry, (key, value) ->
                entry.replace(key, value.toString())
            }
        }
    }
}