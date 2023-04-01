package me.xezard.devmc.drazex.discord

import discord4j.core.DiscordClient
import discord4j.core.GatewayDiscordClient
import discord4j.core.`object`.presence.ClientActivity
import discord4j.core.`object`.presence.ClientPresence
import jakarta.annotation.PostConstruct
import me.xezard.devmc.drazex.discord.events.EventsHandler
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import java.util.logging.Logger

@Component
class DrazexBot(
    private val eventsHandler: EventsHandler,

    @Value("\${discord.token}")
    private var token: String,

    @Value("\${discord.channel-ids.news}")
    var newsChannelId: String,

    @Value("\${discord.messages-color}")
    var messagesColor: String
) {
    lateinit var discord: DiscordClient

    @PostConstruct
    fun init() {
        this.discord = DiscordClient.create(this.token)

        this.discord.gateway()
                    .setInitialPresence {
                        ClientPresence.online(ClientActivity.playing("Minecraft"))
                    }
                    .withGateway { gateway: GatewayDiscordClient ->
                        this.eventsHandler.registerAllHandlers(gateway)
                    }
                    .subscribe()
    }
}