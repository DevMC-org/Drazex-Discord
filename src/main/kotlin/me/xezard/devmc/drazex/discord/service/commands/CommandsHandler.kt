package me.xezard.devmc.drazex.discord.service.commands

import discord4j.core.DiscordClient
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class CommandsHandler (
    private val handlers: List<ICommandHandler>
) {
    fun registerAllHandlers(discordClient: DiscordClient): Mono<Void> {
        return discordClient.applicationId.flatMapMany { appId ->
            discordClient.guilds.flatMap { guild ->
                Flux.fromIterable(this.handlers).flatMap { handler ->
                    discordClient.applicationService.createGuildApplicationCommand(
                            appId,
                            guild.id().asLong(),
                            handler.register()
                    )
                }
            }
        }.then()
    }

    fun findHandlerByCommandName(name: String): ICommandHandler? {
        return this.handlers.find { handler -> handler.name() == name }
    }
}