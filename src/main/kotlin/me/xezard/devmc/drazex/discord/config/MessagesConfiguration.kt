package me.xezard.devmc.drazex.discord.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

@Configuration
@PropertySource(value = ["classpath:messages.yml"], factory = YamlPropertySourceFactory::class)
class MessagesConfiguration (
    @Value("\${new.resource}")
    val newResourcePostTemplate: Array<String>,

    @Value("\${new.resource-version}")
    val newResourceVersionPostTemplate: Array<String>,

    @Value("\${new.article}")
    val newArticlePostTemplate: Array<String>
)