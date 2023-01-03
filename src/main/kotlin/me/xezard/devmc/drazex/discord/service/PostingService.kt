package me.xezard.devmc.drazex.discord.service

import discord4j.common.util.Snowflake
import discord4j.core.spec.EmbedCreateSpec
import discord4j.discordjson.json.EmbedData
import discord4j.discordjson.json.EmbedImageData
import discord4j.discordjson.json.EmbedThumbnailData
import jakarta.annotation.PostConstruct
import me.xezard.devmc.drazex.discord.DrazexBot;
import me.xezard.devmc.drazex.discord.domain.model.post.DiscordPost
import me.xezard.devmc.drazex.discord.domain.model.post.DiscordPostType
import me.xezard.devmc.drazex.vk.config.YamlPropertySourceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.awt.Color
import java.util.*
import java.util.logging.Logger

@Service
@Configuration
@PropertySource(value = ["classpath:messages.yml"], factory = YamlPropertySourceFactory::class)
class PostingService (
    @Autowired
    var bot: DrazexBot,

    @Value("\${messages.new.resource}")
    private var newResourcePostTemplate: Array<String>,

    @Value("\${messages.new.resource-version}")
    private var newResourceVersionPostTemplate: Array<String>,

    @Value("\${messages.new.article}")
    private var newArticlePostTemplate: Array<String>
) {
    // <post type, post template>
    private val postTemplates: MutableMap<DiscordPostType, Array<String>?> = EnumMap(DiscordPostType::class.java)

    @PostConstruct
    fun initMappings() {
        this.postTemplates[DiscordPostType.RESOURCE] = newResourcePostTemplate
        this.postTemplates[DiscordPostType.RESOURCE_VERSION] = newResourceVersionPostTemplate
        this.postTemplates[DiscordPostType.ARTICLE] = newArticlePostTemplate
    }

    fun generatePost(post: DiscordPost): Mono<Void> {
        return this.publish(post.toMessage(this.postTemplates[post.type]!!), post.imageUrl)
    }

    private fun publish(message: String, imageUrl: String): Mono<Void> {
        return this.bot.discord.getChannelById(Snowflake.of(this.bot.newsChannelId))
            .createMessage(EmbedCreateSpec.builder()
                .title("DevMC")
                .url("https://devmc.org")
                .color(discord4j.rest.util.Color.of(Color.decode(this.bot.messagesColor).rgb))
                .description(message)
                .image(imageUrl)
                .thumbnail("https://devmc.org/seo/android-chrome-512x512.png")
                .build()
                .asRequest())
            .then()
    }
}