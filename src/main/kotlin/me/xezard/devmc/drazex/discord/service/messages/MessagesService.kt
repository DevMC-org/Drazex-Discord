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
    fun jsonToEmbed(json: String): EmbedCreateSpec? {
        return try {
            val map = objectMapper.readValue(json, Map::class.java)

            EmbedCreateSpec.builder().apply {
                map["title"]?.toString()?.let { title(it) }
                map["description"]?.toString()?.let { description(it) }
                map["url"]?.toString()?.let { url(it) }
                map["color"]?.toString()?.let { getColorFromString(it)?.let { color -> color(color) } }
                map["timestamp"]?.toString()?.let { Instant.parse(it)?.let { instant -> timestamp(instant) }}

                (map["thumbnail"] as? Map<*, *>)?.let { thumbnailMap ->
                    thumbnailMap["url"]?.toString()?.let { thumbnail(it) }
                }

                (map["image"] as? Map<*, *>)?.let { imageMap ->
                    imageMap["url"]?.toString()?.let { image(it) }
                }

                (map["author"] as? Map<*, *>)?.let { authorMap ->
                    val name = authorMap["name"]?.toString()
                    val iconUrl = authorMap["icon_url"]?.toString()
                    val url = authorMap["url"]?.toString()

                    if (name != null) {
                        author(name, url, iconUrl)
                    }
                }

                (map["fields"] as? List<Map<String, Any>>)?.forEach { field ->
                    val name = field["name"]?.toString()
                    val value = field["value"]?.toString()
                    val inline = field["inline"] as? Boolean ?: false

                    if (name != null && value != null) {
                        addField(name, value, inline)
                    }
                }
            }.build()
        } catch (ex: Exception) {
            null
        }
    }

    fun getColorFromString(colorString: String?): Color? {
        return colorString?.toIntOrNull(16)?.let { Color.of(it) }
    }
}