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
package me.xezard.devmc.drazex.discord.service.messages

import com.fasterxml.jackson.databind.ObjectMapper
import discord4j.core.spec.EmbedCreateSpec
import discord4j.rest.util.Color
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class MessagesService (
    private val objectMapper: ObjectMapper
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

    fun jsonToEmbed(json: String) =
        try {
            val map = this.objectMapper.readValue(json, Map::class.java)

            EmbedCreateSpec.builder().apply {
                map[TITLE_JSON_KEY]?.toString()?.let { title(it) }
                map[DESCRIPTION_JSON_KEY]?.toString()?.let { description(it) }
                map[URL_JSON_KEY]?.toString()?.let { url(it) }
                map[COLOR_JSON_KEY]?.toString()?.let { color(getColorFromString(it)) }
                map[TIMESTAMP_JSON_KEY]?.toString()?.let { Instant.parse(it)?.let(::timestamp) }
                map[THUMBNAIL_JSON_KEY]?.let { this.processThumbnail(it) }
                map[IMAGE_JSON_KEY]?.let { this.processImage(it) }
                map[AUTHOR_JSON_KEY]?.let { this.processAuthor(it) }
                map[FIELDS_JSON_KEY]?.let { this.processFields(it) }
                map[FOOTER_JSON_KEY]?.let { this.processFooter(it) }
            }.build()
        } catch (ex: Exception) {
            null
        }

    private fun EmbedCreateSpec.Builder.processThumbnail(thumbnail: Any) {
        (thumbnail as? Map<*, *>)?.let { thumbnailMap ->
            thumbnailMap[URL_JSON_KEY]?.toString()?.let { this.thumbnail(it) }
        }
    }

    private fun EmbedCreateSpec.Builder.processImage(image: Any) {
        (image as? Map<*, *>)?.let { imageMap ->
            imageMap[URL_JSON_KEY]?.toString()?.let { image(it) }
        }
    }

    private fun EmbedCreateSpec.Builder.processAuthor(author: Any) {
        (author as? Map<*, *>)?.let {
            val name = it[NAME_JSON_KEY]?.toString()
            val iconUrl = it[ICON_URL_JSON_KEY]?.toString()
            val url = it[URL_JSON_KEY]?.toString()

            if (name == null) {
                return
            }

            this.author(name, url, iconUrl)
        }
    }

    private fun EmbedCreateSpec.Builder.processFields(fields: Any) {
        (fields as? List<Map<String, Any>>)?.forEach {
            val name = it[NAME_JSON_KEY]?.toString()
            val value = it[VALUE_JSON_KEY]?.toString()
            val inline = it[INLINE_JSON_KEY] as? Boolean ?: false

            if (name == null || value == null) {
                return
            }

            this.addField(name, value, inline)
        }
    }

    private fun EmbedCreateSpec.Builder.processFooter(footer: Any) {
        (footer as? Map<*, *>)?.let {
            val text = it[TEXT_JSON_KEY]?.toString() ?: ""
            val iconUrl = it[ICON_URL_JSON_KEY]?.toString()

            this.footer(text, iconUrl)
        }
    }

    fun getColorFromString(colorString: String?) =
        Color.of(java.awt.Color.decode(colorString).rgb)
}