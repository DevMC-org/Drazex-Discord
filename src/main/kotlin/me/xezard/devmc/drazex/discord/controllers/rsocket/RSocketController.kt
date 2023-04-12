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
package me.xezard.devmc.drazex.discord.controllers.rsocket

import me.xezard.devmc.drazex.discord.domain.model.post.DiscordPostMapper
import me.xezard.devmc.drazex.discord.domain.model.web.requests.ArticleRequest
import me.xezard.devmc.drazex.discord.domain.model.web.requests.ResourceRequest
import me.xezard.devmc.drazex.discord.domain.model.web.requests.ResourceVersionRequest
import me.xezard.devmc.drazex.discord.service.NewsService
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Mono

@Controller
class RSocketController(
    private var newsService: NewsService,
    private var telegramPostMapper: DiscordPostMapper
) {
    @MessageMapping("resource")
    fun onNewResource(request: ResourceRequest): Mono<Void> {
        return this.newsService.publishNews(this.telegramPostMapper.fromResourceRequest(request))
    }

    @MessageMapping("resource-version")
    fun onNewResourceVersion(request: ResourceVersionRequest): Mono<Void> {
        return this.newsService.publishNews(this.telegramPostMapper.fromResourceVersionRequest(request))
    }

    @MessageMapping("article")
    fun onNewArticle(request: ArticleRequest): Mono<Void> {
        return this.newsService.publishNews(this.telegramPostMapper.fromArticleRequest(request))
    }
}