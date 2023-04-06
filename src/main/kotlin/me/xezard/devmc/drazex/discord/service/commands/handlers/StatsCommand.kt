/*
 *  Drazex-Discord
 *  Discord-bot for the project community devmc.org,
 *  designed to automate administrative tasks, notifications
 *  and other functionality related to the functioning of the community
 *  Copyright (C) 2023 Ivan `Xezard` Zotov
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package me.xezard.devmc.drazex.discord.service.commands.handlers

import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent
import discord4j.discordjson.json.ApplicationCommandRequest
import discord4j.rest.util.Color
import me.xezard.devmc.drazex.discord.service.app.AppService
import me.xezard.devmc.drazex.discord.service.commands.ICommandHandler
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class StatsCommand (
    private val appService: AppService
): ICommandHandler {
    override fun handle(event: ApplicationCommandInteractionEvent): Mono<Void> {
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