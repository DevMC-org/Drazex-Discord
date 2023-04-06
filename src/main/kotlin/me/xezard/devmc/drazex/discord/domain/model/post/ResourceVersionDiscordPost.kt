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
    private val replacesMap = mapOf(
        "{platforms}" to platforms.joinToString(", "),
        "{versions}" to versions.joinToString(", "),
        "{version}" to version
    ) + super.replaces

    override val replaces: MutableMap<String, Any?>
        get() = replacesMap.toMutableMap()
}