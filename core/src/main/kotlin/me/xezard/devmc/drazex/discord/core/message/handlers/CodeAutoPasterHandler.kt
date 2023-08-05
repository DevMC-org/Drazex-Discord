/*
 *  Drazex-Discord
 *  Discord-bot for the project community devmc.org,
 *  designed to automate administrative tasks, notifications
 *  and other functionality related to the functioning of the community
 *  Copyright (C) 2023 Ivan `Xezard` Zotov
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package me.xezard.devmc.drazex.discord.core.message.handlers

import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.`object`.entity.Attachment
import discord4j.core.`object`.entity.Message
import me.xezard.devmc.drazex.discord.core.app.DiscordService
import me.xezard.devmc.drazex.discord.core.config.discord.messages.MessagesProperties
import me.xezard.devmc.drazex.discord.core.message.MessageHandler
import me.xezard.devmc.drazex.discord.core.message.service.MessageService
import me.xezard.devmc.drazex.discord.integration.paste.service.PasteService
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class CodeAutoPasterHandler (
    private val discordService: DiscordService,
    private val pasteService: PasteService,
    private val messageService: MessageService,
    private val messagesProperties: MessagesProperties
) : MessageHandler {
    companion object {
        private val SUPPORTED_ATTACHMENT_FORMATS = listOf(
            "txt", "json", "xml", "csv", "html", "css", "js", "py",
            "java", "kt", "cpp", "c", "rb", "php", "sql", "yaml", "ini",
            "asm", "sh", "bat", "swift", "go", "rust", "lua", "perl", "log",
            "powershell", "scala", "groovy", "dart", "haskell", "typescript",
            "kotlin", "bash", "makefile", "dockerfile", "yaml", "txt", "env"
        )

        private const val URL_REPLACE_PLACEHOLDER = "{url}"
    }

    private val client = WebClient.create()

    override fun handle(event: MessageCreateEvent): Mono<Void> {
        val attachments = this.filterAttachments(event.message.attachments)
        val message = Flux.fromIterable(attachments)
            .flatMap { this.retrieveFile(it.url) }
            .collectList()
            .flatMap { this.pasteService.upload(it) }
            .onErrorResume { Mono.empty() }
            .map { this.messageService.embedFrom(this.messagesProperties.paste, mapOf(
                URL_REPLACE_PLACEHOLDER to it
            )) }

        return message.flatMap { event.message.channel.flatMap {
            channel -> channel.createMessage(it)
        }}.then()
    }

    private fun retrieveFile(url: String) =
        DataBufferUtils.join(this.client.get()
            .uri(url)
            .retrieve()
            .bodyToFlux(DataBuffer::class.java))

    private fun filterAttachments(attachments: List<Attachment>): List<Attachment> =
        attachments.filter { StringUtils.getFilenameExtension(it.filename)?.let { extension ->
            SUPPORTED_ATTACHMENT_FORMATS.contains(extension)
        } ?: false }

    override fun handled(message: Message) =
        !this.discordService.authorIsBot(message) && message.attachments.isNotEmpty()
}