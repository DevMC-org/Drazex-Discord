package me.xezard.devmc.drazex.discord.domain.model.web.requests

data class ArticleRequest (
    val tags: List<String>,
    val categories: List<String>,

    val user: UserMeta,

    val title: String,
    val description: String,
    val slug: String,
    var thumbnail: String,
    val id: Long
)