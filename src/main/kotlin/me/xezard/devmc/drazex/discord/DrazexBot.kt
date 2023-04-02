package me.xezard.devmc.drazex.discord

import discord4j.core.DiscordClient
import discord4j.core.GatewayDiscordClient
import discord4j.core.`object`.presence.ClientActivity
import discord4j.core.`object`.presence.ClientPresence
import jakarta.annotation.PostConstruct
import me.xezard.devmc.drazex.discord.config.DiscordConfiguration
import me.xezard.devmc.drazex.discord.service.events.EventsHandler
import org.springframework.stereotype.Component

@Component
class DrazexBot(
    private val eventsHandler: EventsHandler,

    private val configuration: DiscordConfiguration
) {
    lateinit var discord: DiscordClient

    @PostConstruct
    fun init() {
        this.discord = DiscordClient.create(this.configuration.token)

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