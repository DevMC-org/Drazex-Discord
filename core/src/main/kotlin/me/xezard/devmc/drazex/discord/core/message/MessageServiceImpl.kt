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
package me.xezard.devmc.drazex.discord.core.message

import discord4j.core.spec.EmbedCreateSpec
import discord4j.rest.util.Color
import me.xezard.devmc.drazex.discord.core.message.builder.EmbedCreateSpecBuilder
import org.springframework.stereotype.Service

@Service
class MessageServiceImpl (
    private val builders: List<EmbedCreateSpecBuilder<*>>
) : MessageService {
    override fun embedFrom(value: Any, replaces: Map<String, String>?): EmbedCreateSpec? {
        val builder = this.builders.find { it.supports(value) } ?: return null

        // at this stage, we are sure that the found builder
        // supports working with the value passed to the method
        @Suppress("UNCHECKED_CAST")
        val typedBuilder = builder as EmbedCreateSpecBuilder<Any>
        return typedBuilder.from(value, replaces)
    }

    override fun replace(value: String, replaces: Map<String, String>?): String {
        return replaces?.entries?.fold(value) { acc, (key, replacement) ->
            acc.replace(key, replacement)
        } ?: value
    }

    override fun getColorFromString(colorString: String?): Color =
        Color.of(java.awt.Color.decode(colorString).rgb)
}