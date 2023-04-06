/*
 * Drazex-Discord
 * Discord-bot for the project community devmc.org,
 * designed to automate administrative tasks, notifications
 * and other functionality related to the functioning of the community
 * Copyright (C) 2023 Ivan `Xezard` Zotov
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package me.xezard.devmc.drazex.discord.domain.model.post

import me.xezard.devmc.drazex.discord.domain.model.web.requests.ArticleRequest
import me.xezard.devmc.drazex.discord.domain.model.web.requests.ResourceRequest
import me.xezard.devmc.drazex.discord.domain.model.web.requests.ResourceVersionRequest
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
interface DiscordPostMapper {
    @Mapping(target = "imageUrl", source = "request.bannerUrl")
    fun fromResourceRequest(request: ResourceRequest): ResourceDiscordPost

    @Mapping(target = "platforms", source = "request.supportedPlatforms")
    @Mapping(target = "versions", source = "request.supportedVersions")
    @Mapping(target = "description", source = "request.content")
    @Mapping(target = "imageUrl", source = "request.bannerUrl")
    fun fromResourceVersionRequest(request: ResourceVersionRequest): ResourceVersionDiscordPost

    @Mapping(target = "imageUrl", source = "request.thumbnail")
    fun fromArticleRequest(request: ArticleRequest): ArticleDiscordPost
}