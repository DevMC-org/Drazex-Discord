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
import discord4j.core.`object`.command.ApplicationCommandInteraction
import discord4j.core.`object`.command.ApplicationCommandInteractionOption
import discord4j.core.`object`.command.ApplicationCommandInteractionOptionValue
import discord4j.core.`object`.command.ApplicationCommandOption
import discord4j.core.`object`.entity.channel.GuildChannel
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec
import discord4j.discordjson.json.ApplicationCommandOptionData
import discord4j.discordjson.json.ApplicationCommandRequest
import discord4j.discordjson.json.MessageEditRequest
import discord4j.rest.util.Permission
import me.xezard.devmc.drazex.discord.service.commands.CommandHandler
import me.xezard.devmc.drazex.discord.service.messages.MessagesService
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class EmbedCommand (
    private val messagesService: MessagesService
): CommandHandler {
    companion object {
        private const val COMMAND = "embed"
        private const val CHANNEL_OPTION_NAME = "channel"
        private const val DESCRIPTION = "Send embed message"
        private const val CHANNEL_OPTION_DESCRIPTION = "The channel in which to edit the message"
        private const val MESSAGE_ID_OPTION_NAME = "message-id"
        private const val MESSAGE_ID_OPTION_DESCRIPTION = "The message to edit"
        private const val JSON_OPTION_NAME = "json"
        private const val JSON_OPTION_DESCRIPTION = "Serialized in json embed message"
        private const val EMBED_MESSAGE_SUCCESSFULLY_SENDED = "Embed сообщение успешно отправлено!"
        private const val EMBED_MESSAGE_SUCCESSFULLY_EDITED = "Embed сообщение успешно отредактировано!"
    }

    override val name
        get() = COMMAND

    // '/embed {json}' -> send an embed message to the same channel in which the command was used
    // '/embed {json} {channel id}' -> send an embed message to specified channel
    // '/embed {json} {channel id} {message id}' -> edit an existing message
    override fun handle(event: ApplicationCommandInteractionEvent): Mono<Void> {
        val interaction = event.interaction
        val commandInteraction = interaction.commandInteraction.orElse(null) ?: return Mono.empty()

        val jsonOption = commandInteraction.getOption(JSON_OPTION_NAME)
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString)
                .orElse(null)

        val embed = this.messagesService.jsonToEmbed(jsonOption) ?: return Mono.empty()
        val channelSnowflake = this.extractSnowflake(CHANNEL_OPTION_NAME, commandInteraction)
        val messageSnowflake = this.extractSnowflake(MESSAGE_ID_OPTION_NAME, commandInteraction)
        val targetChannel = channelSnowflake?.let { snowflake ->
            interaction.guild.flatMap { it.getChannelById(snowflake) }
        } ?: interaction.channel.cast(GuildChannel::class.java)
        val targetMessage = messageSnowflake?.let { snowflake -> targetChannel.map {
            it.restChannel.getRestMessage(snowflake)
        }} ?: Mono.empty()

        val successMessage = targetMessage.hasElement().flatMap {
            val content = if (it) EMBED_MESSAGE_SUCCESSFULLY_EDITED
                          else EMBED_MESSAGE_SUCCESSFULLY_SENDED

            event.reply(InteractionApplicationCommandCallbackSpec
                    .builder()
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

    fun extractSnowflake(optionName: String, commandInteraction: ApplicationCommandInteraction): Snowflake? =
        commandInteraction.getOption(optionName)
            .flatMap(ApplicationCommandInteractionOption::getValue)
            .map(ApplicationCommandInteractionOptionValue::asString)
            .map(Snowflake::of)
            .orElse(null)

    override fun register(): ApplicationCommandRequest =
        ApplicationCommandRequest.builder()
            .name(COMMAND)
            .description(DESCRIPTION)
            .defaultMemberPermissions(Permission.ADMINISTRATOR.value.toString())
            .addAllOptions(listOf(
                ApplicationCommandOptionData.builder()
                    .name(JSON_OPTION_NAME)
                    .description(JSON_OPTION_DESCRIPTION)
                    .type(ApplicationCommandOption.Type.STRING.value)
                    .required(true)
                    .build(),

                ApplicationCommandOptionData.builder()
                    .name(CHANNEL_OPTION_NAME)
                    .description(CHANNEL_OPTION_DESCRIPTION)
                    .type(ApplicationCommandOption.Type.STRING.value)
                    .required(false)
                    .build(),

                ApplicationCommandOptionData.builder()
                    .name(MESSAGE_ID_OPTION_NAME)
                    .description(MESSAGE_ID_OPTION_DESCRIPTION)
                    .type(ApplicationCommandOption.Type.STRING.value)
                    .required(false)
                    .build()
            )).build()
}