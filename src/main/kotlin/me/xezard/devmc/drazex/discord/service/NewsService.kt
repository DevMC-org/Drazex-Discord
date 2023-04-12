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
package me.xezard.devmc.drazex.discord.service

import discord4j.common.util.Snowflake
import discord4j.core.spec.EmbedCreateSpec
import me.xezard.devmc.drazex.discord.DrazexBot
import me.xezard.devmc.drazex.discord.config.DiscordConfiguration
import me.xezard.devmc.drazex.discord.config.MessagesConfiguration
import me.xezard.devmc.drazex.discord.domain.model.post.DiscordPost
import me.xezard.devmc.drazex.discord.domain.model.post.DiscordPostType
import me.xezard.devmc.drazex.discord.service.messages.MessagesService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class NewsService (
    private val bot: DrazexBot,

    private val messagesService: MessagesService,

    private val discordConfiguration: DiscordConfiguration,
    private val messagesConfiguration: MessagesConfiguration
) {
    // <post type, post template>
    private val postTemplates = mutableMapOf<DiscordPostType, Array<String>?>().apply {
        this[DiscordPostType.RESOURCE] = messagesConfiguration.newResourcePostTemplate
        this[DiscordPostType.RESOURCE_VERSION] = messagesConfiguration.newResourceVersionPostTemplate
        this[DiscordPostType.ARTICLE] = messagesConfiguration.newArticlePostTemplate
    }

    fun publishNews(post: DiscordPost): Mono<Void> {
        return this.generatePost(this.assembleMessage(post), post.imageUrl)
    }

    private fun generatePost(message: String, imageUrl: String): Mono<Void> {
        val embed = EmbedCreateSpec.builder()
                .title("DevMC")
                .url(this.discordConfiguration.baseUrl)
                .color(this.messagesService.getColorFromString(this.discordConfiguration.messagesColor))
                .description(message)
                .image(imageUrl)
                .thumbnail(this.discordConfiguration.thumbnailUrl)
                .build()
                .asRequest()

        return this.bot.discord.getChannelById(Snowflake.of(this.discordConfiguration.newsConsumerChannelId))
                .createMessage(embed)
                .then()
    }

    private fun assembleMessage(post: DiscordPost): String {
        val template = this.postTemplates[post.type]!!
        val baseUrl = this.discordConfiguration.baseUrl
        val replaces = post.replaces

        replaces["{url}"] = "${baseUrl}/${replaces["{url}"]}"

        return template.joinToString(separator = "\n") { message ->
            replaces.entries.fold(message) { entry, (key, value) ->
                entry.replace(key, value.toString())
            }
        }
    }
}