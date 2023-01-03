package me.xezard.devmc.drazex.discord.domain.model.web.requests

import me.xezard.devmc.drazex.discord.domain.model.resources.ResourceType

data class ResourceVersionRequest (
    val supportedPlatforms: List<String>,
    val supportedVersions: List<String>,

    val user: UserMeta,

    val resourceType: ResourceType,

    val title: String,
    val content: String,
    val version: String,
    val slug: String,
    var bannerUrl: String
)