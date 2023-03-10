package me.xezard.devmc.drazex.discord.commands.listeners

import discord4j.core.GatewayDiscordClient
import discord4j.core.event.domain.lifecycle.ReadyEvent
import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.`object`.entity.User
import discord4j.core.`object`.entity.channel.MessageChannel
import me.xezard.devmc.drazex.discord.DrazexBot
import reactor.core.publisher.Mono


class CommandRegistrator(
    private var bot: DrazexBot
) {
    private val channelId: String = bot.newsChannelId
    fun registerCommand(): Mono<Void> {
        val login: Mono<Void> = this.bot.discord.withGateway { gateway: GatewayDiscordClient ->

            val printOnLogin: Mono<Void> =
                gateway.on(ReadyEvent::class.java) { event ->
                Mono.fromRunnable<Void>(
                    Runnable {
                        val self: User = event.self
                        System.out.printf(
                            "Logged in as %s#%s%n",
                            self.username,
                            self.discriminator
                        )
                    })
            }
                .then()

            val handlePingCommand: Mono<Void> =
                gateway.on(MessageCreateEvent::class.java) { event: MessageCreateEvent ->
                    val message = event.message
                    if (message.content.equals("!ping", ignoreCase = true)) {
                        return@on message.channel
                            .flatMap { channel: MessageChannel ->
                                channel.createMessage(
                                    "pong!"
                                )
                            }
                    }
                    return@on Mono.empty()
                }
                    .then()
            return@withGateway handlePingCommand.and(printOnLogin)
        }
        return login
    }
}

//        val login: Mono<Void> = this.bot.discord.withGateway { gateway: GatewayDiscordClient ->
//            gateway.on(MessageCreateEvent::class.java { event: MessageCreateEvent ->
//                    val message: Message = event.getMessage()
//                    if (message.getContent().equalsIgnoreCase("!ping")) {
//                        return message.getChannel()
//                            .flatMap { channel -> channel.createMessage("pong!") }
//                    }
//                },
//            )
//        }
//        return login


//    var login: Mono<Void> = client.withGateway { gateway: GatewayDiscordClient ->
//        gateway.on(
//            MessageCreateEvent::class.java,
//            Function<E, Publisher<T>> { event: E ->
//                val message: Message = event.getMessage()
//                if (message.getContent().equalsIgnoreCase("!ping")) {
//                    return@on message.getChannel()
//                        .flatMap { channel -> channel.createMessage("pong!") }
//                }
//                Mono.empty()
//            })
//    }

//        return this.bot.discord.withGateway { gateway: GatewayDiscordClient ->
//            gateway.on(MessageCreateEvent::class.java) { event: MessageCreateEvent -> {
//
//                    val self: Optional<User> = event.message.author
//                    println("Logged in as иди нахуй")
//
//                    val embed: EmbedCreateSpec = EmbedCreateSpec.builder().color(Color.GREEN).title("Статус: Живой")
//                        .description("я онлайн.")
//                        .thumbnail("https://i.imgur.com/FMiS7Xg.jpg")
//                        .build()
//
//                    return bot.discord.getChannelById(Snowflake.of(channelId))
//                        .createMessage(embed.asRequest()).then()

//                }
//            }.then()
//        }.then()


