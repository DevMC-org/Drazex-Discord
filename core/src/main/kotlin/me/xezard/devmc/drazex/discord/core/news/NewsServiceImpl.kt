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
package me.xezard.devmc.drazex.discord.core.news

import discord4j.common.util.Snowflake
import discord4j.discordjson.json.EmbedData
import me.xezard.devmc.drazex.discord.core.DrazexBot
import me.xezard.devmc.drazex.discord.core.config.discord.DiscordProperties
import me.xezard.devmc.drazex.discord.core.config.discord.channels.ChannelsProperties
import me.xezard.devmc.drazex.discord.core.config.discord.messages.MessagesProperties
import me.xezard.devmc.drazex.discord.core.message.service.MessageService
import me.xezard.devmc.drazex.discord.core.model.post.DiscordPost
import me.xezard.devmc.drazex.discord.core.model.post.DiscordPost.Companion.URL_REPLACE_PLACEHOLDER
import me.xezard.devmc.drazex.discord.core.model.post.DiscordPostType
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class NewsServiceImpl (
    private val bot: DrazexBot,

    private val messageService: MessageService,

    private val properties: DiscordProperties,
    private val channelsProperties: ChannelsProperties,
    private val messagesProperties: MessagesProperties
) : NewsService {
    companion object {
        private const val MESSAGE_TEMPLATE_SEPARATOR = "\n"
        private const val BASE_URL_REPLACE_PLACEHOLDER = "{base-url}"
        private const val RESOURCE_REPLACE_PLACEHOLDER = "{resource}"
        private const val URL = "$BASE_URL_REPLACE_PLACEHOLDER/$RESOURCE_REPLACE_PLACEHOLDER"
    }

    // <post type, post template>
    private val postTemplates by lazy {
        mapOf(
            DiscordPostType.RESOURCE to this.messagesProperties.reposts.resource,
            DiscordPostType.RESOURCE_VERSION to this.messagesProperties.reposts.resourceVersion,
            DiscordPostType.ARTICLE to this.messagesProperties.reposts.article
        )
    }

    override fun publishNews(post: DiscordPost): Mono<Void> =
        this.assembleEmbed(post)?.let { this.sendMessage(it) } ?: Mono.empty()

    private fun assembleEmbed(post: DiscordPost): EmbedData? {
        val properties = this.postTemplates[post.type] ?: return null
        val descriptionTemplate = properties.description ?: return null
        val description = this.assembleDescription(post.replaces, descriptionTemplate)

        return this.messageService.embedFrom(properties)
            ?.withDescription(description)
            ?.withImage(post.imageUrl)
            ?.asRequest()
    }

    private fun assembleDescription(replaces: MutableMap<String, String>, description: String): String {
        val baseUrl = this.properties.baseUrl

        replaces[URL_REPLACE_PLACEHOLDER] = URL
            .replace(BASE_URL_REPLACE_PLACEHOLDER, baseUrl)
            .replace(RESOURCE_REPLACE_PLACEHOLDER, replaces[URL_REPLACE_PLACEHOLDER] ?: "")

        return description.split(MESSAGE_TEMPLATE_SEPARATOR).joinToString(separator = MESSAGE_TEMPLATE_SEPARATOR) {
            replaces.entries.fold(it) { entry, (key, value) -> entry.replace(key, value) }
        }
    }

    private fun sendMessage(embed: EmbedData) =
        this.bot.discord
            .getChannelById(Snowflake.of(this.channelsProperties.news.consumer))
            .createMessage(embed)
            .then()
}