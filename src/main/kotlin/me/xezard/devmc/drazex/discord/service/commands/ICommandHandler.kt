package me.xezard.devmc.drazex.discord.service.commands

import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent
import discord4j.discordjson.json.ApplicationCommandRequest
import reactor.core.publisher.Mono

interface ICommandHandler {
    fun handle(event: ApplicationCommandInteractionEvent): Mono<Void>

    fun register(): ApplicationCommandRequest

    fun name(): String
}