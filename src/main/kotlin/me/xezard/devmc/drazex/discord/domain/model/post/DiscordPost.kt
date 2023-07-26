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
package me.xezard.devmc.drazex.discord.domain.model.post

open class DiscordPost(
    var type: DiscordPostType,

    title: String,
    description: String,
    url: String,
    var imageUrl: String
) {
    companion object {
        const val URL_REPLACE_PLACEHOLDER = "{url}"
        private const val TITLE_REPLACE_PLACEHOLDER = "{title}"
        private const val DESCRIPTION_REPLACE_PLACEHOLDER = "{description}"
    }

    open val replaces = mapOf(
        URL_REPLACE_PLACEHOLDER to url,
        TITLE_REPLACE_PLACEHOLDER to title,
        DESCRIPTION_REPLACE_PLACEHOLDER to description
    ).toMutableMap()
}