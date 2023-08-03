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
package me.xezard.devmc.drazex.discord.core.commands

import discord4j.discordjson.json.ApplicationCommandRequest
import me.xezard.devmc.drazex.discord.core.config.discord.commands.CommandProperties

abstract class AbstractCommandHandler (
    private val commandsService: CommandsService,
    private val properties: CommandProperties
) : CommandHandler {
    override val name
        get() = this.properties.name

    override fun register(): ApplicationCommandRequest =
        this.commandsService.createCommand(this.properties)
}