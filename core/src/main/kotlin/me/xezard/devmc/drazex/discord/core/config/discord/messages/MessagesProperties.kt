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
package me.xezard.devmc.drazex.discord.core.config.discord.messages

import me.xezard.devmc.drazex.discord.core.config.discord.messages.properties.discord.DiscordEmbedMessageProperties
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties
data class MessagesProperties (
    val stats: DiscordEmbedMessageProperties,
    val news: DiscordEmbedMessageProperties,
    val request: DiscordEmbedMessageProperties,
    val paste: DiscordEmbedMessageProperties,
    val reposts: RepostsMessagesProperties
)