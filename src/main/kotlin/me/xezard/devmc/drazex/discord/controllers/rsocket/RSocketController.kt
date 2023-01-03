package me.xezard.devmc.drazex.discord.controllers.rsocket


import me.xezard.devmc.drazex.discord.domain.model.post.DiscordPostMapper
import me.xezard.devmc.drazex.discord.domain.model.web.requests.ArticleRequest
import me.xezard.devmc.drazex.discord.domain.model.web.requests.ResourceRequest
import me.xezard.devmc.drazex.discord.domain.model.web.requests.ResourceVersionRequest
import me.xezard.devmc.drazex.discord.service.PostingService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Mono
import java.util.logging.Logger

@Controller
class RSocketController(
    @Autowired
    private var postingService: PostingService,
) {
    @Autowired
    private lateinit var telegramPostMapper: DiscordPostMapper

    @MessageMapping("resource")
    fun onNewResource(request: ResourceRequest): Mono<Void> {
        return this.postingService.generatePost(this.telegramPostMapper.fromResourceRequest(request))
    }

    @MessageMapping("resource-version")
    fun onNewResourceVersion(request: ResourceVersionRequest): Mono<Void> {
        return this.postingService.generatePost(this.telegramPostMapper.fromResourceVersionRequest(request))
    }

    @MessageMapping("article")
    fun onNewArticle(request: ArticleRequest): Mono<Void> {
        return this.postingService.generatePost(this.telegramPostMapper.fromArticleRequest(request))
    }
}