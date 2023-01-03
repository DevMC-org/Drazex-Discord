package me.xezard.devmc.drazex.discord.domain.model.web.requests

import me.xezard.devmc.drazex.discord.domain.model.resources.ResourceType

data class ResourceRequest (
    val tags: List<String>,
    val categories: List<String>,

    val user: UserMeta,

    val resourceType: ResourceType,

    val title: String,
    val description: String,
    val slug: String,
    var bannerUrl: String,
    val wikiUrl: String?,
    val sourceCodeUrl: String?
)