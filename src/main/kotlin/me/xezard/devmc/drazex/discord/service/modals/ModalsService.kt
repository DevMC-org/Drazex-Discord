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
package me.xezard.devmc.drazex.discord.service.modals

import discord4j.core.`object`.component.ActionRow
import discord4j.core.`object`.component.TextInput
import discord4j.core.spec.InteractionPresentModalSpec
import me.xezard.devmc.drazex.discord.config.discord.modals.properties.DiscordModalInputProperties
import me.xezard.devmc.drazex.discord.config.discord.modals.properties.DiscordModalProperties
import org.springframework.stereotype.Service

@Service
class ModalsService {
    fun createModal(properties: DiscordModalProperties): InteractionPresentModalSpec {
        val id = properties.id
        val actions = this.createActionRows(properties.inputs, id)

        return InteractionPresentModalSpec.builder()
            .customId(id)
            .title(properties.title)
            .components(actions)
            .build()
    }

    fun createActionRows(inputs: Map<String, DiscordModalInputProperties>, id: String) =
        inputs.map { (name, config) ->
            val input = TextInput.small(
                "$id-$name",
                config.description,
                config.limits?.length?.minimum ?: 0,
                config.limits?.length?.maximum ?: Int.MAX_VALUE
            ).required()

            config.placeholder?.let { input.placeholder(it) }

            ActionRow.of(input)
        }

    fun getInputValue(inputs: List<TextInput>, id: String) =
        inputs.find { it.customId == id }?.data?.value()?.get()
}