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
package me.xezard.devmc.drazex.discord.integration.paste.service

import com.google.common.io.CharStreams
import me.xezard.devmc.drazex.discord.integration.paste.config.PasteProperties
import me.xezard.devmc.drazex.discord.integration.paste.dto.requests.CodePasteFileContent
import me.xezard.devmc.drazex.discord.integration.paste.dto.requests.CodePasteFileRequest
import me.xezard.devmc.drazex.discord.integration.paste.dto.requests.CodePasteRequest
import me.xezard.devmc.drazex.discord.integration.paste.dto.responses.CodePasteResponse
import me.xezard.devmc.drazex.discord.integration.paste.dto.responses.CodePasteResponseStatus
import me.xezard.devmc.drazex.discord.integration.paste.exception.PasteException
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.InputStreamReader

@Service
class PasteServiceImpl (
    private val pasteProperties: PasteProperties,
    private val webClient: WebClient
) : PasteService {
    companion object {
        private const val PASTE_CONTENT_FORMAT = "text"
    }

    override fun upload(content: List<DataBuffer>): Mono<String> =
        Flux.fromIterable(content)
            .map { this.createFileRequest(it) }
            .collectList()
            .map { CodePasteRequest(it) }
            .flatMap { this.send(it) }
            .flatMap {
                when (it.status) {
                    CodePasteResponseStatus.SUCCESS ->
                        Mono.just("${this.pasteProperties.serviceUrls.file}${it.result.id}")
                    CodePasteResponseStatus.ERROR -> Mono.error(PasteException())
                }
            }

    private fun send(request: CodePasteRequest) =
        this.webClient.post()
            .uri(this.pasteProperties.serviceUrls.api)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .retrieve()
            .bodyToMono(CodePasteResponse::class.java)

    private fun createFileRequest(buffer: DataBuffer): CodePasteFileRequest {
        val value = CharStreams.toString(InputStreamReader(buffer.asInputStream()))
        return CodePasteFileRequest(CodePasteFileContent(PASTE_CONTENT_FORMAT, value))
    }
}