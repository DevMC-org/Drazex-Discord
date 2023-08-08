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
package me.xezard.devmc.drazex.discord.ai.service

import me.xezard.devmc.drazex.discord.ai.config.AiProperties
import me.xezard.devmc.drazex.discord.ai.dto.AiMessageDto
import me.xezard.devmc.drazex.discord.ai.dto.AiModelType
import me.xezard.devmc.drazex.discord.ai.dto.AiRoleType
import me.xezard.devmc.drazex.discord.ai.dto.requests.AiCompletionRequest
import me.xezard.devmc.drazex.discord.ai.dto.responses.completion.AiCompletionResponse
import me.xezard.devmc.drazex.discord.ai.exception.AiException
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class AiServiceImpl (
    private val aiProperties: AiProperties,
    private val webClient: WebClient
) : AiService {
    companion object {
        private const val CHAT_COMPLETION_ROUTE = "/chat/completions"
        private const val BEARER_TOKEN_REPLACE_PLACEHOLDER = "{token}"
        private const val BEARER_TOKEN_HEADER_VALUE = "Bearer $BEARER_TOKEN_REPLACE_PLACEHOLDER"
    }

    override fun request(model: AiModelType, text: String): Mono<String> {
        val request = AiCompletionRequest(
            model = model,
            messages = listOf(AiMessageDto(
                role = AiRoleType.USER,
                content = text
            )),
            maxTokens = 8192,
            allowFallback = true
        )

        return this.send(request)
            .onErrorMap { AiException(it) }
            .map { it.choices }
            .flatMapMany { Flux.fromIterable(it) }
            .next()
            .map { it.message.content }
    }

    private fun send(request: AiCompletionRequest) =
        this.webClient.post()
            .uri("${this.aiProperties.serviceUrls.api}${CHAT_COMPLETION_ROUTE}")
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN_HEADER_VALUE
                .replace(BEARER_TOKEN_REPLACE_PLACEHOLDER, this.aiProperties.token))
            .bodyValue(request)
            .retrieve()
            .bodyToMono(AiCompletionResponse::class.java)
}