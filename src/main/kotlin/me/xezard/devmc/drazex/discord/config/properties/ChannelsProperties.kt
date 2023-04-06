package me.xezard.devmc.drazex.discord.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("channels.ids")
class ChannelsProperties {
    lateinit var showcase: List<String>
}