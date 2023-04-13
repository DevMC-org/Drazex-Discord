package me.xezard.devmc.drazex.discord.service.events.handlers

import com.google.common.io.CharStreams
import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.`object`.entity.Attachment
import discord4j.core.`object`.entity.Message
import me.xezard.devmc.drazex.discord.domain.model.web.requests.File
import me.xezard.devmc.drazex.discord.domain.model.web.requests.FileContent
import me.xezard.devmc.drazex.discord.domain.model.web.requests.PastePost
import me.xezard.devmc.drazex.discord.domain.model.web.requests.PasteResponse
import me.xezard.devmc.drazex.discord.service.channels.ChannelsHandler
import me.xezard.devmc.drazex.discord.service.events.IEventHandler
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.InputStreamReader
import java.util.*
import java.util.logging.Level

@Component
class CodeToPasteHandler(
    private val channelsHandler: ChannelsHandler,
    private val client: WebClient
): IEventHandler<MessageCreateEvent> {
    override fun handle(event: MessageCreateEvent): Mono<Void> {
        return Mono.justOrEmpty(event.message)
            .flatMap(::processMessage)
            .then()
            .onErrorResume(::handleError)
    }

    private fun processMessage(message: Message): Mono<Void> {
        val client = WebClient.create()

        return message.attachments.takeIf { it.isNotEmpty() }?.let { attachments ->
            processAttachments(client, attachments)
                    .flatMap { result -> message.channel.flatMap { it.createMessage(result) }}
                    .then()
        } ?: Mono.empty()
    }

    private fun processAttachments(client: WebClient, attachments: List<Attachment>): Mono<String> {
        return Flux.fromIterable(attachments)
                .flatMap { attachment ->
                    client.get()
                            .uri(attachment.url)
                            .retrieve()
                            .bodyToFlux(DataBuffer::class.java)
                }
                .map { buffer -> CharStreams.toString(InputStreamReader(buffer.asInputStream())) }
                .map { File(FileContent("text", it)) }
                .collectList()
                .map { PastePost(it) }
                .flatMap { pastePost ->
                    client.post()
                            .uri("https://api.paste.gg/v1/pastes")
                            .body(BodyInserters.fromValue(pastePost))
                            .retrieve()
                            .bodyToMono(PasteResponse::class.java)
                }
                .map { pasteResponse -> pasteResponse.result.files.map { file -> file.id } }
                .flatMap { list ->
                    Mono.just(list.joinToString("\n"))
                }
    }

    private fun handleError(exception: Throwable): Mono<Void> {
        return Mono.fromRunnable {
            MessageCreateHandler.LOGGER.log(
                Level.WARNING, "An unknown error occurred: " +
                    Objects.requireNonNullElse(exception.message, ""), exception)
        }
    }

    override fun getEvent(): Class<out MessageCreateEvent> {
        return MessageCreateEvent::class.java
    }
}