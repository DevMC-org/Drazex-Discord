package me.xezard.devmc.drazex.discord.domain.model.post

import me.xezard.devmc.drazex.discord.domain.model.resources.ResourceType

class ResourceVersionDiscordPost (
    private val platforms: List<String>,
    private val versions: List<String>,

    resourceType: ResourceType,

    title: String,
    description: String,
    private val version: String,
    slug: String,
    imageUrl: String
) : DiscordPost(DiscordPostType.RESOURCE_VERSION, title,
    description, "${resourceType.name.lowercase()}/$slug/versions", imageUrl) {
    override val replaces: MutableMap<String, Any?>
        get() {
            val replaces = super.replaces

            replaces["{platforms}"] = this.platforms.joinToString(separator = ", ")
            replaces["{versions}"] = this.versions.joinToString(separator = ", ")
            replaces["{version}"] = this.version

            return replaces
        }
}