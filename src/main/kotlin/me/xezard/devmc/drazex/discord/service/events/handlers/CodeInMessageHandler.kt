package me.xezard.devmc.drazex.discord.service.events.handlers

import com.google.common.io.CharStreams
import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.`object`.entity.Attachment
import discord4j.core.`object`.entity.Message
import discord4j.core.spec.EmbedCreateSpec
import me.xezard.devmc.drazex.discord.domain.model.web.requests.paste.CodePasteFileContent
import me.xezard.devmc.drazex.discord.domain.model.web.requests.paste.CodePasteFileRequest
import me.xezard.devmc.drazex.discord.domain.model.web.requests.paste.CodePasteRequest
import me.xezard.devmc.drazex.discord.domain.model.web.responses.paste.CodePasteResponse
import me.xezard.devmc.drazex.discord.service.events.IEventHandler
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.InputStreamReader

@Component
class CodeInMessageHandler: IEventHandler<MessageCreateEvent> {
    companion object {
        const val PASTE_API_URL = "https://api.paste.gg/v1/pastes"
    }

    private val client = WebClient.create()

    override fun handle(event: MessageCreateEvent): Mono<Void> {
        return Mono.justOrEmpty(event.message).flatMap(this::processMessage)
    }

    private fun processMessage(message: Message): Mono<Void> {
        val attachments = message.attachments

        return if (attachments.isNotEmpty()) {
            val pasteId = this.processAttachments(attachments)

            pasteId.flatMap {
                val embed = EmbedCreateSpec.builder()
                        .title("Твой текст залит на paste.gg")
                        .description("[Нажми, чтобы открыть!](https://paste.gg/p/anonymous/$it)")
                        .build()

                message.channel.flatMap { message -> message.createMessage(embed) }
                               .then(message.delete())
            }
        } else {
            Mono.empty()
        }
    }

    private fun processAttachments(attachments: List<Attachment>): Mono<String> {
        return Flux.fromIterable(attachments)
                .flatMap { this.retrieveFile(it.url) }
                .map { CodePasteFileRequest(CodePasteFileContent("text",
                       CharStreams.toString(InputStreamReader(it.asInputStream())))) }
                .collectList()
                .map { CodePasteRequest(it) }
                .flatMap { this.sendToPasteService(it) }
                .map { it.result.id }
    }

    private fun sendToPasteService(post: CodePasteRequest): Mono<CodePasteResponse> {
        return this.client.post()
                .uri(PASTE_API_URL)
                .body(BodyInserters.fromValue(post))
                .retrieve()
                .bodyToMono(CodePasteResponse::class.java)
    }

    private fun retrieveFile(url: String): Mono<DataBuffer> {
        return DataBufferUtils.join(this.client.get()
                .uri(url)
                .retrieve()
                .bodyToFlux(DataBuffer::class.java))
    }

    override fun getEvent(): Class<out MessageCreateEvent> {
        return MessageCreateEvent::class.java
    }
}