package me.xezard.devmc.drazex.discord.service.Serializers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component


@Component
class EmbedSerializer(
    @Autowired
    private val applicationProps: ApplicationProps
) {

}