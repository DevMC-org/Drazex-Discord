package me.xezard.devmc.drazex.discord.domain.model.post

class ArticleDiscordPost (
    title: String,
    description: String,
    slug: String,
    imageUrl: String,

    id: Long
) : DiscordPost(
    DiscordPostType.ARTICLE,
    title, description,
    "article/$slug.$id",
    imageUrl
)