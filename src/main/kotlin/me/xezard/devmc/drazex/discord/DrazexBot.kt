package me.xezard.devmc.drazex.discord

import discord4j.core.DiscordClient
import discord4j.core.`object`.presence.ClientActivity
import discord4j.core.`object`.presence.ClientPresence
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class DrazexBot(
    @Value("\${discord.token}")
    private var botToken: String,

    @Value("\${discord.channel-ids.news}")
    var newsChannelId: String,

    @Value("\${discord.messages-color}")
    var messagesColor: String
) {
    lateinit var discord: DiscordClient

    @PostConstruct
    fun init() {
        this.discord = DiscordClient.create(this.botToken)

        this.discord.gateway()
            .setInitialPresence { ClientPresence.online(ClientActivity.playing("Minecraft")) }
    }
}