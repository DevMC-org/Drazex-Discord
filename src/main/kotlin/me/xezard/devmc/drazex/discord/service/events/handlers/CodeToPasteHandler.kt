package me.xezard.devmc.drazex.discord.service.events.handlers

import com.google.common.io.CharStreams
import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.`object`.entity.Message
import me.xezard.devmc.drazex.discord.domain.model.web.requests.File
import me.xezard.devmc.drazex.discord.domain.model.web.requests.FileContent
import me.xezard.devmc.drazex.discord.domain.model.web.requests.PastePost
import me.xezard.devmc.drazex.discord.domain.model.web.requests.PasteResponse
import me.xezard.devmc.drazex.discord.service.channels.ChannelsHandler
import me.xezard.devmc.drazex.discord.service.events.IEventHandler
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserter
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.InputStreamReader
import java.util.*
import java.util.logging.Level

@Component
class CodeToPasteHandler(
    private val channelsHandler: ChannelsHandler
): IEventHandler<MessageCreateEvent> {
    companion object {
        val WEBCLIENT = WebClient.create()
    }
    override fun handle(event: MessageCreateEvent): Mono<Void> {
        return Mono.justOrEmpty(event.message)
            .flatMap(::processMessage)
            .then()
            .onErrorResume(::handleError)
    }
    private fun processMessage(message: Message): Mono<Void> {

//         if author is bot -> skip
//        if (message.author.map { user -> user.isBot }.orElse(false)) {
//            return Mono.empty()
//        }
//        if (message.attachments.isEmpty()) return Mono.empty()
//            return Flux.fromIterable(message.attachments).flatMap { attachment ->
//                 WEBCLIENT.post()
//                    .retrieve()
//                    .bodyToMono(PasteResponse::class.java)
//                    .flatMapMany {result ->
//                        Mono.just(result.result.id)
//                    }
//            }.collectList().flatMap {list ->
//                message.channel.flatMap { channel ->
//                    channel.createMessage(list.joinToString("\n"))
//                }
//            }.then()
            return message.attachments.takeIf { it.isNotEmpty() }
                ?.let { attachments ->
                    Flux.fromIterable(attachments)
                        .flatMap { attachment ->
                            attachments.map {
                                WEBCLIENT.get().uri(attachment.url).retrieve().bodyToFlux(DataBuffer::class.java)
                                    .map { CharStreams.toString(InputStreamReader(it.asInputStream())) }
                                    .map { File(FileContent("text", it)) }
                                    .collectList()
                                    .map { PastePost(it) }
                                    .flatMap { pastePost ->
                                        WEBCLIENT
                                            .post()
                                            .uri("https://api.paste.gg/v1/pastes")
                                            .body(BodyInserters.fromValue(pastePost))
                                            .retrieve()
                                            .bodyToMono(PasteResponse::class.java)
                                            .map { pasteResponse -> pasteResponse.result.files.map { file -> file.id } }
                                            .flatMap { list -> message.channel.flatMap { it.createMessage(list.joinToString("\n")) } }
                                    }

                            }
                        }


                } ?: Mono.empty()
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