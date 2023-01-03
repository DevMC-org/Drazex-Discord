package me.xezard.devmc.drazex.discord.domain.model.resources

import com.fasterxml.jackson.annotation.JsonProperty

enum class ResourceType {
    @JsonProperty("plugin")
    PLUGIN,

    @JsonProperty("mod")
    MOD,

    @JsonProperty("build")
    BUILD,

    @JsonProperty("configuration")
    CONFIGURATION,

    @JsonProperty("resourcepack")
    RESOURCEPACK,

    @JsonProperty("graphics")
    GRAPHICS,

    @JsonProperty("model")
    MODEL,

    @JsonProperty("other")
    OTHER
}