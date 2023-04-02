package me.xezard.devmc.drazex.discord.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties("channels.ids.news")
class NewsChannelsProperties {
    lateinit var publishers: List<String>
}