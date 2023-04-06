package me.xezard.devmc.drazex.discord.domain.model.post

open class DiscordPost(
    var type: DiscordPostType,

    title: String,
    description: String,
    url: String,
    var imageUrl: String
) {
    open val replaces: MutableMap<String, Any?> = mapOf(
        "{url}" to url,
        "{title}" to title,
        "{description}" to description
    ).toMutableMap()
}