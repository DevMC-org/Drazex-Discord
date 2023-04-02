package me.xezard.devmc.drazex.discord.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.codec.Decoder
import org.springframework.core.codec.Encoder
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.messaging.rsocket.RSocketStrategies

@Configuration
class RSocketConfiguration {
    @Bean
    fun strategies(): RSocketStrategies {
        return RSocketStrategies.builder()
            .encoders { encoders: MutableList<Encoder<*>> -> encoders.add(Jackson2JsonEncoder()) }
            .decoders { decoders: MutableList<Decoder<*>> -> decoders.add(Jackson2JsonDecoder()) }
            .build()
    }
}