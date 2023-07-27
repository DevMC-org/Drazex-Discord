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

import discord4j.core.`object`.entity.Message
import discord4j.core.`object`.reaction.ReactionEmoji
import me.xezard.devmc.drazex.discord.config.channels.properties.ChannelsProperties
import me.xezard.devmc.drazex.discord.service.channels.ChannelHandler
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class ShowcaseChannelsHandler(
    private val channelsProperties: ChannelsProperties
): ChannelHandler {
    companion object {
        private const val COOL_EMOJI = "👍"
        private const val NOT_COOL_EMOJI = "👎"
        private const val COOKIE_EMOJI = "🍪"

        private val MESSAGE_EMOJIS = listOf(COOL_EMOJI, NOT_COOL_EMOJI, COOKIE_EMOJI)
    }

    override val handledChannelIds
        get() = channelsProperties.showcase

    override fun handle(message: Message): Mono<Void> {
        if (message.author.isEmpty) {
            return Mono.empty()
        }

        return MESSAGE_EMOJIS.fold(Mono.empty()) { mono, emoji ->
            mono.then(message.addReaction(ReactionEmoji.unicode(emoji)))
        }
    }
}