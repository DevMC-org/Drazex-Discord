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
package me.xezard.devmc.drazex.discord.core.modals.handlers

import discord4j.common.util.Snowflake
import discord4j.core.event.domain.interaction.ModalSubmitInteractionEvent
import discord4j.core.`object`.component.ActionRow
import discord4j.core.`object`.component.Button
import discord4j.core.`object`.component.TextInput
import discord4j.core.spec.EmbedCreateFields
import discord4j.discordjson.json.MessageCreateRequest
import me.xezard.devmc.drazex.discord.core.app.DiscordService.Companion.DISCORD_USER_URL
import me.xezard.devmc.drazex.discord.core.buttons.handlers.RequestDeleteButtonHandler
import me.xezard.devmc.drazex.discord.core.config.discord.messages.MessagesProperties
import me.xezard.devmc.drazex.discord.core.config.discord.modals.properties.DiscordModalProperties
import me.xezard.devmc.drazex.discord.core.message.MessageService
import me.xezard.devmc.drazex.discord.core.modals.AbstractModalHandler
import me.xezard.devmc.drazex.discord.core.modals.ModalsService
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

abstract class RequestModalHandler (
    private val modalsService: ModalsService,
    private val messageService: MessageService,
    private val messagesProperties: MessagesProperties,
    private val discordModalProperties: DiscordModalProperties
) : AbstractModalHandler(modalsService, discordModalProperties) {
    companion object {
        private const val ID_REPLACE_PLACEHOLDER = "{id}"
        private const val EMBED_PUBLISHED_BY_FIELD = "Опубликовал:"
        private const val REQUEST_SUCCESSFULLY_PUBLISHED_MESSAGE = "Ваш запрос успешно опубликован!"
        private const val DELETE_BUTTON_LABEL = "❌"
    }

    fun handle(
        event: ModalSubmitInteractionEvent,
        channelsIds: List<String>,
        title: String? = null
    ): Mono<Void> {
        val member = event.interaction.member.orElse(null) ?: return Mono.empty()
        val id = member.id.asString()
        val inputs = event.getComponents(TextInput::class.java)

        var embed = this.messageService.embedFrom(this.messagesProperties.request) ?: return Mono.empty()

        embed = embed.withAuthor(EmbedCreateFields.Author.of(
            member.displayName,
            DISCORD_USER_URL.replace(ID_REPLACE_PLACEHOLDER, id),
            member.avatarUrl
        ))

        val fields = mutableListOf<EmbedCreateFields.Field>()

        this.getInputsMapping(this.discordModalProperties).forEach { (field, inputId) ->
            this.modalsService.getInputValue(inputs, inputId)?.let {
                if (field == "Тип услуги:") {
                    embed = embed.withTitle("$title $it")
                } else {
                    fields.add(EmbedCreateFields.Field.of(field, it, false))
                }
            }
        }

        fields.add(EmbedCreateFields.Field.of(EMBED_PUBLISHED_BY_FIELD, member.mention, false))

        embed = embed.withFields(fields)

        val actionRow = ActionRow.of(Button.secondary(
            RequestDeleteButtonHandler.BUTTON_ID + id,
                DELETE_BUTTON_LABEL
        ))
        val message = MessageCreateRequest.builder()
            .embed(embed.asRequest())
            .build()
            .withComponents(actionRow.data)
        val channels = event.interaction.guild.flatMapMany {
            Flux.fromIterable(channelsIds.map(Snowflake::of)).flatMap(it::getChannelById)
        }
        val messages = channels.flatMap { it.restChannel.createMessage(message) }
        val reply = event.reply(REQUEST_SUCCESSFULLY_PUBLISHED_MESSAGE).withEphemeral(true)

        return messages.then(reply)
    }

    private fun getInputsMapping(properties: me.xezard.devmc.drazex.discord.core.config.discord.modals.properties.DiscordModalProperties) =
        properties.inputs.map { (name, data) -> data.description to "${properties.id}-$name" }.toMap()
}