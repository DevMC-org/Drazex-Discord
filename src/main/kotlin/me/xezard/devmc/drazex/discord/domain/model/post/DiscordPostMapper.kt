package me.xezard.devmc.drazex.discord.domain.model.post

import me.xezard.devmc.drazex.discord.domain.model.web.requests.ArticleRequest
import me.xezard.devmc.drazex.discord.domain.model.web.requests.ResourceRequest
import me.xezard.devmc.drazex.discord.domain.model.web.requests.ResourceVersionRequest
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
interface DiscordPostMapper {
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "url", ignore = true)
    @Mapping(target = "imageUrl", source = "request.bannerUrl")
    fun fromResourceRequest(request: ResourceRequest): ResourceDiscordPost

    @Mapping(target = "platforms", source = "request.supportedPlatforms")
    @Mapping(target = "versions", source = "request.supportedVersions")
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "description", source = "request.content")
    @Mapping(target = "url", ignore = true)
    @Mapping(target = "imageUrl", source = "request.bannerUrl")
    fun fromResourceVersionRequest(request: ResourceVersionRequest): ResourceVersionDiscordPost

    @Mapping(target = "type", ignore = true)
    @Mapping(target = "url", ignore = true)
    @Mapping(target = "imageUrl", source = "request.thumbnail")
    fun fromArticleRequest(request: ArticleRequest): ArticleDiscordPost
}