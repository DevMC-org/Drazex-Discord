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

import me.xezard.devmc.drazex.discord.domain.model.resources.ResourceType

class ResourceVersionDiscordPost (
    platforms: List<String>,
    versions: List<String>,

    resourceType: ResourceType,

    title: String,
    description: String,
    version: String,
    slug: String,
    imageUrl: String
) : DiscordPost(
    DiscordPostType.RESOURCE_VERSION,
    title,
    description,
    "${resourceType.name.lowercase()}/$slug/versions",
    imageUrl
) {
    companion object {
        private const val PLATFORMS_REPLACE_PLACEHOLDER = "{platforms}"
        private const val VERSIONS_REPLACE_PLACEHOLDER = "{versions}"
        private const val VERSION_REPLACE_PLACEHOLDER = "{version}"
        private const val VALUES_SEPARATOR = ", "
    }

    private val replacesMap = mapOf(
        PLATFORMS_REPLACE_PLACEHOLDER to platforms.joinToString(VALUES_SEPARATOR),
        VERSIONS_REPLACE_PLACEHOLDER to versions.joinToString(VALUES_SEPARATOR),
        VERSION_REPLACE_PLACEHOLDER to version
    ) + super.replaces

    override val replaces
        get() = this.replacesMap.toMutableMap()
}