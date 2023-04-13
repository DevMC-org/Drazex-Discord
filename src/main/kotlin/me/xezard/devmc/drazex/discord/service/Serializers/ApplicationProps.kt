package me.xezard.devmc.drazex.discord.service.Serializers

import com.fasterxml.jackson.core.type.TypeReference
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import org.yaml.snakeyaml.Yaml

@Component
class ApplicationProps {
    class Embed(
        var title: String? = null,
        val description: String? = null,
        val color: String? = null,
        val author: AuthorConfig? = null,
        val footer: FooterConfig? = null,
        val image: ImageConfig? = null,
        val fields: List<FieldConfig>? = null
    )


    data class AuthorConfig(
        val name: String,
    )

    data class FooterConfig(
        val text: String,
    )

    data class ImageConfig(
        val url: String
    )

    data class FieldConfig(
        val name: String,
        val value: String,
        val inline: Boolean = false
    )
}