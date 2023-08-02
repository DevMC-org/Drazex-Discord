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
package me.xezard.devmc.drazex.discord.service.commands.handlers

import discord4j.common.util.Snowflake
import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent
import discord4j.core.`object`.entity.channel.GuildChannel
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec
import discord4j.discordjson.json.MessageEditRequest
import me.xezard.devmc.drazex.discord.config.discord.commands.CommandsProperties
import me.xezard.devmc.drazex.discord.service.commands.AbstractCommandHandler
import me.xezard.devmc.drazex.discord.service.commands.CommandsService
import me.xezard.devmc.drazex.discord.service.messages.MessagesService
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class EmbedCommand (
    private val commandsService: CommandsService,
    private val messagesService: MessagesService,
    commandsProperties: CommandsProperties
): AbstractCommandHandler(commandsService, commandsProperties.embed) {
    companion object {
        private const val CHANNEL_OPTION_NAME = "channel"
        private const val MESSAGE_ID_OPTION_NAME = "message-id"
        private const val JSON_OPTION_NAME = "json"
        private const val EMBED_MESSAGE_SUCCESSFULLY_SENDED = "Embed сообщение успешно отправлено!"
        private const val EMBED_MESSAGE_SUCCESSFULLY_EDITED = "Embed сообщение успешно отредактировано!"
    }

    // '/embed {json}' -> send an embed message to the same channel in which the command was used
    // '/embed {json} {channel id}' -> send an embed message to specified channel
    // '/embed {json} {channel id} {message id}' -> edit an existing message
    override fun handle(event: ApplicationCommandInteractionEvent): Mono<Void> {
        val json = this.commandsService.extractValue(event, JSON_OPTION_NAME)
        val embed = json?.let { this.messagesService.embedFrom(it) } ?: return Mono.empty()
        val channelId = this.commandsService.extractValue(event, CHANNEL_OPTION_NAME)
        val messageId = this.commandsService.extractValue(event, MESSAGE_ID_OPTION_NAME)
        val targetChannel = channelId?.let { id -> event.interaction.guild.flatMap {
            it.getChannelById(Snowflake.of(id))
        }} ?: event.interaction.channel.cast(GuildChannel::class.java)
        val targetMessage = messageId?.let { id -> targetChannel.map {
            it.restChannel.getRestMessage(Snowflake.of(id))
        }} ?: Mono.empty()

        val successMessage = targetMessage.hasElement().flatMap {
            val content = if (it) EMBED_MESSAGE_SUCCESSFULLY_EDITED
            else EMBED_MESSAGE_SUCCESSFULLY_SENDED

            event.reply(InteractionApplicationCommandCallbackSpec.builder()
                .content(content)
                .ephemeral(true)
                .build())
        }

        return targetMessage.flatMap {
            it.edit(MessageEditRequest.builder()
                .embed(embed.asRequest())
                .build())
        }.switchIfEmpty(targetChannel.flatMap { it.restChannel.createMessage(embed.asRequest()) })
            .then(successMessage)
    }
}