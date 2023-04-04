package me.xezard.devmc.drazex.discord.domain.model.post

open class DiscordPost (
    var type: DiscordPostType,

    title: String,
    description: String,
    url: String,
    var imageUrl: String
) {
    private companion object {
        const val URL_PREFIX = "https://devmc.org/"
    }

    open val replaces: MutableMap<String, Any?> = mapOf(
            "{url}" to "$URL_PREFIX$url",
            "{title}" to title,
            "{description}" to description
    ).toMutableMap()

    fun toMessage(template: Array<String>): String {
        var message = template.joinToString(separator = "\n")

        for ((key, value) in replaces) {
            message = message.replace(key, value.toString())
        }

        return message
    }
}