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
package me.xezard.devmc.drazex.discord.service.messages.builder.list

import discord4j.core.spec.EmbedCreateSpec
import me.xezard.devmc.drazex.discord.config.discord.messages.properties.discord.DiscordEmbedMessageProperties
import me.xezard.devmc.drazex.discord.service.messages.MessagesService
import me.xezard.devmc.drazex.discord.service.messages.builder.AbstractEmbedCreateSpecBuilder
import me.xezard.devmc.drazex.discord.service.messages.builder.fields.DiscordEmbedAuthor
import me.xezard.devmc.drazex.discord.service.messages.builder.fields.DiscordEmbedField
import me.xezard.devmc.drazex.discord.service.messages.builder.fields.DiscordEmbedFooter
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

@Component
class PropertiesEmbedCreateSpecBuilder (
    @Lazy
    messagesService: MessagesService
): AbstractEmbedCreateSpecBuilder<DiscordEmbedMessageProperties> (
    messagesService
) {
    override fun from(
        value: DiscordEmbedMessageProperties,
        replaces: Map<String, String>?
    ): EmbedCreateSpec? =
        super.create(
            replaces,
            value.fields?.let { fields -> fields.map { DiscordEmbedField(it.name, it.value, it.inline) }},
            value.author?.let { DiscordEmbedAuthor(it.name, it.url, it.iconUrl) },
            value.footer?.let { DiscordEmbedFooter(it.text, it.iconUrl) },
            value.title,
            value.description,
            value.url,
            value.color,
            value.image,
            value.thumbnail,
            null
        )
}