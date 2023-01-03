package me.xezard.devmc.drazex.discord.domain.model.post

open class DiscordPost (
    var type: DiscordPostType,
    private var title: String,
    private var description: String,
    var url: String,
    var imageUrl: String
) {
    protected open val replaces: MutableMap<String, Any?>
        get() {
            val replaces: MutableMap<String, Any?> = HashMap()

            replaces["{url}"] = "https://devmc.org/${this.url}"
            replaces["{title}"] = this.title
            replaces["{description}"] = this.description

            return replaces
        }

    fun toMessage(template: Array<String>): String {
        var message = template.joinToString(separator = "\n")

        for (entry in this.replaces.entries.iterator()) {
            message = message.replace(entry.key, entry.value.toString())
        }

        return message
    }
}