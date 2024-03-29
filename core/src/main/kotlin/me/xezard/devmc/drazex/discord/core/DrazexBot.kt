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
package me.xezard.devmc.drazex.discord.core

import discord4j.core.DiscordClient
import discord4j.core.`object`.presence.ClientActivity
import discord4j.core.`object`.presence.ClientPresence
import jakarta.annotation.PostConstruct
import me.xezard.devmc.drazex.discord.core.commands.CommandsHandler
import me.xezard.devmc.drazex.discord.core.config.discord.DiscordProperties
import me.xezard.devmc.drazex.discord.core.events.EventsHandler
import org.springframework.stereotype.Component

@Component
class DrazexBot(
    private val eventsHandler: EventsHandler,
    private val commandsHandler: CommandsHandler,

    private val properties: DiscordProperties
) {
    companion object {
        private const val PRESENCE_PLAYING = "Minecraft"

        private val PRESENCE = ClientPresence.online(ClientActivity.playing(PRESENCE_PLAYING))
    }

    lateinit var discord: DiscordClient

    @PostConstruct
    fun init() {
        this.discord = DiscordClient.create(this.properties.token)

        this.discord.gateway()
            .setInitialPresence { PRESENCE }
            .withGateway(this.eventsHandler::registerAllHandlers)
            .subscribe()

        this.commandsHandler.registerAllHandlers(this.discord).subscribe()
    }
}