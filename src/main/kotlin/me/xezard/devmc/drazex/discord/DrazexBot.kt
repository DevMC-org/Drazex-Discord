package me.xezard.devmc.drazex.discord

import discord4j.core.DiscordClient
import discord4j.core.GatewayDiscordClient
import discord4j.core.event.domain.lifecycle.ReadyEvent
import discord4j.core.`object`.entity.User
import discord4j.core.`object`.presence.ClientActivity
import discord4j.core.`object`.presence.ClientPresence
import jakarta.annotation.PostConstruct
import me.xezard.devmc.drazex.discord.events.EventsHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

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
    @Autowired
    lateinit var eventsHandler: EventsHandler


    @PostConstruct
    fun init() {
        this.discord = DiscordClient.create(this.botToken)

        this.discord.withGateway { gateway: GatewayDiscordClient ->
            eventsHandler.registerAll(gateway)
        }.subscribe()


        this.discord.gateway()
                .setInitialPresence { ClientPresence.online(ClientActivity.playing("Minecraft")) }
                .login()
                .subscribe()
    }
}