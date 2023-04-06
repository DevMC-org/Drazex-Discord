package me.xezard.devmc.drazex.discord.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("channels.ids.news")
class NewsChannelsProperties {
    lateinit var publishers: List<String>
}