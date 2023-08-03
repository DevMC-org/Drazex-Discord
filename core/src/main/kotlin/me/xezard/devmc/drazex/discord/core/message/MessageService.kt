package me.xezard.devmc.drazex.discord.core.message

import discord4j.core.spec.EmbedCreateSpec
import discord4j.rest.util.Color

interface MessageService {
    fun embedFrom(value: Any, replaces: Map<String, String>? = null): EmbedCreateSpec?

    fun replace(value: String, replaces: Map<String, String>?): String

    fun getColorFromString(colorString: String?): Color
}