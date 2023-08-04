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
package me.xezard.devmc.drazex.discord.core.message.builder.list

import com.fasterxml.jackson.databind.ObjectMapper
import me.xezard.devmc.drazex.discord.core.message.service.MessageService
import me.xezard.devmc.drazex.discord.core.message.builder.AbstractEmbedCreateSpecBuilder
import me.xezard.devmc.drazex.discord.core.message.builder.fields.DiscordEmbedAuthor
import me.xezard.devmc.drazex.discord.core.message.builder.fields.DiscordEmbedField
import me.xezard.devmc.drazex.discord.core.message.builder.fields.DiscordEmbedFooter
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class JsonEmbedCreateSpecBuilder (
    @Lazy
    messageService: MessageService,

    private val objectMapper: ObjectMapper
): AbstractEmbedCreateSpecBuilder<String>(
    messageService
) {
    companion object {
        private const val TITLE_JSON_KEY = "title"
        private const val DESCRIPTION_JSON_KEY = "description"
        private const val URL_JSON_KEY = "url"
        private const val COLOR_JSON_KEY = "color"
        private const val TIMESTAMP_JSON_KEY = "timestamp"
        private const val THUMBNAIL_JSON_KEY = "thumbnail"
        private const val IMAGE_JSON_KEY = "image"
        private const val AUTHOR_JSON_KEY = "author"
        private const val NAME_JSON_KEY = "name"
        private const val ICON_URL_JSON_KEY = "icon_url"
        private const val FIELDS_JSON_KEY = "fields"
        private const val VALUE_JSON_KEY = "value"
        private const val INLINE_JSON_KEY = "inline"
        private const val FOOTER_JSON_KEY = "footer"
        private const val TEXT_JSON_KEY = "text"
    }

    override fun from(value: String, replaces: Map<String, String>?) =
        try {
            val map = this.objectMapper.readValue(value, Map::class.java)

            super.create(
                replaces,
                map[FIELDS_JSON_KEY]?.let { this.getFields(it) },
                map[AUTHOR_JSON_KEY]?.let { this.getAuthor(it) },
                map[FOOTER_JSON_KEY]?.let { this.getFooter(it) },
                map[TITLE_JSON_KEY]?.toString(),
                map[DESCRIPTION_JSON_KEY]?.toString(),
                map[URL_JSON_KEY]?.toString(),
                map[COLOR_JSON_KEY]?.toString(),
                map[IMAGE_JSON_KEY]?.toString(),
                map[THUMBNAIL_JSON_KEY]?.toString(),
                map[TIMESTAMP_JSON_KEY]?.toString()?.let { Instant.parse(it) }
            )
        } catch (ex: Exception) {
            null
        }

    private fun getFields(fields: Any): List<DiscordEmbedField> {
        val embedFields = mutableListOf<DiscordEmbedField>()
        val fieldList = fields as? List<Map<*, *>> ?: return embedFields

        for (fieldMap in fieldList) {
            val name = fieldMap[NAME_JSON_KEY]?.toString()
            val value = fieldMap[VALUE_JSON_KEY]?.toString()
            val inline = fieldMap[INLINE_JSON_KEY] as? Boolean ?: false

            if (name == null || value == null) {
                continue
            }

            embedFields.add(DiscordEmbedField(name, value, inline))
        }

        return embedFields
    }

    private fun getAuthor(author: Any) =
        (author as? Map<*, *>)?.let {
            val name = it[NAME_JSON_KEY]?.toString()
            val iconUrl = it[ICON_URL_JSON_KEY]?.toString()
            val url = it[URL_JSON_KEY]?.toString()

            if (name == null) {
                return null
            }

            DiscordEmbedAuthor(name, url, iconUrl)
        }

    private fun getFooter(footer: Any) =
        (footer as? Map<*, *>)?.let {
            val text = it[TEXT_JSON_KEY]?.toString()
            val iconUrl = it[ICON_URL_JSON_KEY]?.toString()

            if (text == null) {
                return null
            }

            DiscordEmbedFooter(text, iconUrl)
        }
}