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
package me.xezard.devmc.drazex.discord.service.app

class DiscordService {
    companion object {
        val DISCORD_EMOJI_PATTERN = Regex("<:\\w+:\\d+>")

        private const val DISCORD_DOMAIN = "discordapp.com"
        const val DISCORD_AVATAR_URL = "https://cdn.$DISCORD_DOMAIN/avatars/{user_id}/{avatar}.png"
        const val DISCORD_CHANNEL_URL = "https://$DISCORD_DOMAIN/channels/{id}"
        const val DISCORD_USER_URL = "https://$DISCORD_DOMAIN/users/{id}"
        const val CHANNEL_NAME_PATTERN = " #\\w+"
    }
}