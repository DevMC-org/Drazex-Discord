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
package me.xezard.devmc.drazex.discord.rest.rsocket

import me.xezard.devmc.drazex.discord.core.news.NewsService
import me.xezard.devmc.drazex.discord.rest.rsocket.dto.requests.ArticleRequest
import me.xezard.devmc.drazex.discord.rest.rsocket.dto.requests.ResourceRequest
import me.xezard.devmc.drazex.discord.rest.rsocket.dto.requests.ResourceVersionRequest
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller

@Controller
class RSocketController (
    private val newsService: NewsService,
    private val postMapper: DiscordPostMapper
) {
    @MessageMapping("resource")
    fun onNewResource(request: ResourceRequest) =
        this.newsService.publishNews(this.postMapper.fromResourceRequest(request))

    @MessageMapping("resource-version")
    fun onNewResourceVersion(request: ResourceVersionRequest) =
        this.newsService.publishNews(this.postMapper.fromResourceVersionRequest(request))

    @MessageMapping("article")
    fun onNewArticle(request: ArticleRequest) =
        this.newsService.publishNews(this.postMapper.fromArticleRequest(request))
}