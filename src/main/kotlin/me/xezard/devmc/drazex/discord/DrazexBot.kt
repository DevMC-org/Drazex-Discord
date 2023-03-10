package me.xezard.devmc.drazex.discord

import discord4j.core.DiscordClient
import discord4j.core.GatewayDiscordClient
import discord4j.core.event.domain.lifecycle.ReadyEvent
import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.`object`.entity.User
import discord4j.core.`object`.entity.channel.MessageChannel
import discord4j.core.`object`.presence.ClientActivity
import discord4j.core.`object`.presence.ClientPresence
import jakarta.annotation.PostConstruct
import me.xezard.devmc.drazex.discord.commands.listeners.CommandRegistrator
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

    @PostConstruct
    fun init() {
        this.discord = DiscordClient.create(this.botToken)
        this.discord.gateway()
            .setInitialPresence {
                ClientPresence.online(ClientActivity.playing("Minecraft"))
            }.login().subscribe()
        this.discord.withGateway { gateway: GatewayDiscordClient ->
            val printOnLogin: Mono<Void> = gateway.on(ReadyEvent::class.java) { event ->
                    Mono.fromRunnable<Void>() {
                        val self: User = event.self
                        System.out.printf(
                            "Logged in as %s#%s%n",
                            self.username,
                            self.discriminator
                        )
                    }
            }
                .then()

            return@withGateway printOnLogin
        }.subscribe()

    }
}