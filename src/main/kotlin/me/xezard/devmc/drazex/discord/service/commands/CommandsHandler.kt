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
package me.xezard.devmc.drazex.discord.service.commands

import discord4j.core.DiscordClient
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class CommandsHandler (
    private val handlers: List<CommandHandler>
) {
    fun registerAllHandlers(discordClient: DiscordClient): Mono<Void> {
        return discordClient.applicationId.flatMapMany { appId ->
            discordClient.guilds.flatMap { guild ->
                Flux.fromIterable(this.handlers).flatMap {
                    discordClient.applicationService.createGuildApplicationCommand(
                            appId,
                            guild.id().asLong(),
                            it.register()
                    )
                }
            }
        }.then()
    }

    fun findHandlerByCommandName(name: String): CommandHandler? =
        this.handlers.find { it.name == name }
}