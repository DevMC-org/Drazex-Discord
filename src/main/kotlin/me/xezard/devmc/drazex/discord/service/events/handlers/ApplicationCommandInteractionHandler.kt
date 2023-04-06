package me.xezard.devmc.drazex.discord.service.events.handlers

import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent
import me.xezard.devmc.drazex.discord.service.commands.CommandsHandler
import me.xezard.devmc.drazex.discord.service.events.IEventHandler
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class ApplicationCommandInteractionHandler (
    private val commandsHandler: CommandsHandler
): IEventHandler<ApplicationCommandInteractionEvent> {
    override fun handle(event: ApplicationCommandInteractionEvent): Mono<Void> {
        return commandsHandler.findHandlerByCommandName(event.commandName)?.handle(event) ?: Mono.empty()
    }

    override fun getEvent(): Class<ApplicationCommandInteractionEvent> {
        return ApplicationCommandInteractionEvent::class.java
    }
}