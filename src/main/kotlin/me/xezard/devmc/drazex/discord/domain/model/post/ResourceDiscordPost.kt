package me.xezard.devmc.drazex.discord.domain.model.post

import me.xezard.devmc.drazex.discord.domain.model.resources.ResourceType

class ResourceDiscordPost (
    resourceType: ResourceType,

    title: String,
    description: String,
    slug: String,
    imageUrl: String
) : DiscordPost(
    DiscordPostType.RESOURCE, title,
    description,
    "${resourceType.name.lowercase()}/$slug",
    imageUrl
)