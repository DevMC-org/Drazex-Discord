package me.xezard.devmc.drazex.discord.service.channels

import discord4j.core.`object`.entity.Message
import reactor.core.publisher.Mono

interface IChannelHandler {
    fun handle(message: Message): Mono<Void>

    fun getHandledChannelIds(): List<String>
}