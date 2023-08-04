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
package me.xezard.devmc.drazex.discord.core.message.builder

import discord4j.core.spec.EmbedCreateFields
import discord4j.core.spec.EmbedCreateSpec
import me.xezard.devmc.drazex.discord.core.message.service.MessageService
import me.xezard.devmc.drazex.discord.core.message.builder.fields.DiscordEmbedAuthor
import me.xezard.devmc.drazex.discord.core.message.builder.fields.DiscordEmbedField
import me.xezard.devmc.drazex.discord.core.message.builder.fields.DiscordEmbedFooter
import java.lang.reflect.ParameterizedType
import java.time.Instant

abstract class AbstractEmbedCreateSpecBuilder<T> (
    private val messageService: MessageService
) : EmbedCreateSpecBuilder<T> {
    override fun supports(value: Any): Boolean {
        val superClass = javaClass.genericSuperclass as ParameterizedType
        val typeArgument = superClass.actualTypeArguments[0]
        return (typeArgument as Class<*>).isInstance(value)
    }

    fun create(
        replaces: Map<String, String>?,
        fields: List<DiscordEmbedField>?,
        author: DiscordEmbedAuthor?,
        footer: DiscordEmbedFooter?,
        title: String?,
        description: String?,
        url: String?,
        color: String?,
        image: String?,
        thumbnail: String?,
        timestamp: Instant?
    ): EmbedCreateSpec =
        EmbedCreateSpec.builder().apply {
            title?.let { title(messageService.replace(it, replaces)) }
            description?.let { description(messageService.replace(it, replaces)) }
            url?.let { url(it) }
            color?.let { color(messageService.getColorFromString(it)) }
            image?.let { image(it) }
            thumbnail?.let { thumbnail(it) }
            timestamp?.let { timestamp(it) }
            footer?.let {
                val text = messageService.replace(it.text, replaces)

                footer(EmbedCreateFields.Footer.of(text, it.iconUrl))
            }
            author?.let {
                val name = messageService.replace(it.name, replaces)

                author(EmbedCreateFields.Author.of(name, it.url, it.iconUrl))
            }
            fields?.let { fields(it.map { properties ->
                val name = messageService.replace(properties.name, replaces)
                val field = messageService.replace(properties.value, replaces)

                EmbedCreateFields.Field.of(name, field, properties.inline ?: false)
            })}
        }.build()
}