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
package me.xezard.devmc.drazex.discord.core.scheduler

import discord4j.common.util.Snowflake
import discord4j.rest.entity.RestChannel
import me.xezard.devmc.drazex.discord.core.DrazexBot
import me.xezard.devmc.drazex.discord.core.config.discord.channels.ChannelsProperties
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

@Component
class RequestsScheduler (
    private val bot: DrazexBot,
    private val channelsProperties: ChannelsProperties
) {
    companion object {
        private val FORMATTER = DateTimeFormatter.ISO_INSTANT
    }

    private val channelIds =
        this.channelsProperties.requests.development +
        this.channelsProperties.requests.team.search +
        this.channelsProperties.requests.team.recruitment

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.HOURS)
    fun deleteOutdatedMessages() {
        Flux.fromIterable(this.channelIds)
                .flatMap { Mono.just(this.bot.discord.getChannelById(Snowflake.of(it))) }
                .flatMap { this.deleteOldMessages(it) }
                .subscribe()
    }

    private fun deleteOldMessages(channel: RestChannel) =
        channel.getMessagesBefore(Snowflake.of(Instant.now()))
            .filter { this.isOutdated(it.timestamp()) }
            .flatMap { channel.getRestMessage(Snowflake.of(it.id().asString())).delete(null) }

    private fun isOutdated(timestamp: String): Boolean {
        val monthAgo = LocalDateTime.now().minusMonths(1)
        val instant = Instant.from(FORMATTER.parse(timestamp))
        val thresholdInstant = monthAgo.atZone(ZoneId.systemDefault()).toInstant()

        return instant.isBefore(thresholdInstant)
    }
}