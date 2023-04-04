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