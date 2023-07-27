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
package me.xezard.devmc.drazex.discord.service.channels.handlers

import discord4j.common.util.Snowflake
import discord4j.core.`object`.entity.Message
import discord4j.core.spec.EmbedCreateSpec
import discord4j.discordjson.json.MessageCreateRequest
import me.xezard.devmc.drazex.discord.config.DiscordConfiguration
import me.xezard.devmc.drazex.discord.config.channels.properties.NewsChannelsProperties
import me.xezard.devmc.drazex.discord.service.app.DiscordService.Companion.CHANNEL_NAME_PATTERN
import me.xezard.devmc.drazex.discord.service.app.DiscordService.Companion.DISCORD_AVATAR_URL
import me.xezard.devmc.drazex.discord.service.app.DiscordService.Companion.DISCORD_CHANNEL_URL
import me.xezard.devmc.drazex.discord.service.app.DiscordService.Companion.DISCORD_EMOJI_PATTERN
import me.xezard.devmc.drazex.discord.service.channels.ChannelHandler
import me.xezard.devmc.drazex.discord.service.messages.MessagesService
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class NewsChannelHandler (
    private val messagesService: MessagesService,

    private val discordConfiguration: DiscordConfiguration,
    private val channelsProperties: NewsChannelsProperties,
    private val newsChannelsProperties: NewsChannelsProperties
): ChannelHandler {
    companion object {
        private const val USER_ID_PLACEHOLDER = "{user_id}"
        private const val USER_AVATAR_PLACEHOLDER = "{avatar}"
        private const val DISCORD_CHANNEL_ID_PLACEHOLDER = "{id}"

        private const val MESSAGE_FOOTER = "• Лента новостей сообщества"
    }

    override val handledChannelIds
        get() = this.newsChannelsProperties.publishers

    override fun handle(message: Message): Mono<Void> {
        val messageData = message.data
        val author = messageData.author()
        val avatar = DISCORD_AVATAR_URL
                .replace(USER_ID_PLACEHOLDER, author.id().asString())
                .replace(USER_AVATAR_PLACEHOLDER, author.avatar().orElse(""))
        val guildId = message.messageReference.flatMap { it.guildId }
                .orElse(Snowflake.of(23423))
                .asString()
        val channelUrl = DISCORD_CHANNEL_URL.replace(DISCORD_CHANNEL_ID_PLACEHOLDER, guildId)
        val embedBuilder = EmbedCreateSpec.builder()
                .author(author.username().replace(CHANNEL_NAME_PATTERN.toRegex(), ""),
                        channelUrl, avatar)
                .color(this.messagesService.getColorFromString(this.discordConfiguration.messagesColor))
                .description(message.content.replace(DISCORD_EMOJI_PATTERN, ""))
                .thumbnail(avatar)
                .footer(MESSAGE_FOOTER, "")

        messageData.attachments().firstOrNull()?.url()?.let { embedBuilder.image(it) }

        val consumerId = Snowflake.of(this.channelsProperties.consumer)
        val messageRequest = MessageCreateRequest.builder()
                .addEmbed(embedBuilder.build().asRequest())

        messageRequest.addAllEmbeds(message.embeds.map { it.data })

        return message.guild.flatMap { it.getChannelById(consumerId) }
                .flatMap { it.restChannel.createMessage(messageRequest.build()) }
                .then()
    }
}