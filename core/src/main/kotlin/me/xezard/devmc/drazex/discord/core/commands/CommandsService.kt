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

import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent
import discord4j.core.`object`.command.ApplicationCommandInteractionOption
import discord4j.core.`object`.command.ApplicationCommandInteractionOptionValue
import discord4j.core.`object`.command.ApplicationCommandOption
import discord4j.discordjson.json.*
import discord4j.rest.util.Permission
import me.xezard.devmc.drazex.discord.core.config.discord.commands.CommandProperties
import me.xezard.devmc.drazex.discord.core.config.discord.commands.option.CommandOptionChoiceProperties
import me.xezard.devmc.drazex.discord.core.config.discord.commands.option.CommandOptionProperties
import org.springframework.stereotype.Service

@Service
class CommandsService {
    fun createCommand(properties: CommandProperties): ImmutableApplicationCommandRequest =
        ApplicationCommandRequest.builder()
            .name(properties.name)
            .description(properties.description)
            .also { properties.permission?.let { permission ->
                it.defaultMemberPermissions(Permission.valueOf(permission).value.toString()) }}
            .also { properties.options?.let { options -> it.options(this.createOptions(options)) }}
            .build()

    fun createOptions(options: Map<String, CommandOptionProperties>) =
        options.map { (name, properties) -> this.createOption(properties, name) }

    fun createOption(properties: CommandOptionProperties, name: String): ImmutableApplicationCommandOptionData =
        ApplicationCommandOptionData.builder()
            .name(name)
            .description(properties.description)
            .type(ApplicationCommandOption.Type.valueOf(properties.type).value)
            .required(properties.required ?: false)
            .also { properties.choices?.let { choices -> it.choices(this.createChoices(choices)) }}
            .build()

    fun createChoices(properties: List<CommandOptionChoiceProperties>) =
        properties.map { this.createChoice(it) }

    fun createChoice(properties: CommandOptionChoiceProperties): ImmutableApplicationCommandOptionChoiceData =
        ApplicationCommandOptionChoiceData.builder()
            .name(properties.name)
            .value(properties.value)
            .build()

    fun extractValue(event: ApplicationCommandInteractionEvent, option: String): String? =
        event.interaction.commandInteraction
            .flatMap { it.getOption(option) }
            .flatMap(ApplicationCommandInteractionOption::getValue)
            .map(ApplicationCommandInteractionOptionValue::asString)
            .orElse(null)
}