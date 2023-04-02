package me.xezard.devmc.drazex.discord.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

@Configuration
@PropertySource(value = ["classpath:discord.yml"], factory = YamlPropertySourceFactory::class)
class DiscordConfiguration {
    @Value("\${token}")
    lateinit var token: String

    @Value("\${messages.color}")
    lateinit var messagesColor: String

    @Value("\${thumbnail.url}")
    lateinit var thumbnailUrl: String

    @Value("\${channels.ids.news.consumer}")
    lateinit var newsConsumerChannelId: String
}