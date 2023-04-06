package me.xezard.devmc.drazex.discord.service.commands.handlers

import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent
import discord4j.discordjson.json.ApplicationCommandRequest
import discord4j.rest.util.Color
import me.xezard.devmc.drazex.discord.service.app.AppService
import me.xezard.devmc.drazex.discord.service.commands.ICommandHandler
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.logging.Logger

@Component
class StatsCommand (
    private val appService: AppService
): ICommandHandler {
    override fun handle(event: ApplicationCommandInteractionEvent): Mono<Void> {
        Logger.getLogger("TEst").warning("CALLED HANDLE IN STATS COMMAND!")

        val (uptime, usedMemory, maximumMemory) = this.appService.replaces.values.map { it.toString() }

        return event.reply {
            it.addEmbed { embed ->
                embed.setTitle("Статистика")
                embed.setColor(Color.of(33, 247, 4))
                embed.addField("Аптайм", uptime, false)
                embed.addField("Память", "Используется: $usedMemory МБ\n" +
                                               "Выделено: $maximumMemory МБ", false)
            }
        }
    }

    override fun register(): ApplicationCommandRequest {
        return ApplicationCommandRequest.builder()
                .name(this.name())
                .description("Display the overall statistics of the bot")
                .defaultPermission(false)
                .build()
    }

    override fun name(): String {
        return "stats"
    }
}