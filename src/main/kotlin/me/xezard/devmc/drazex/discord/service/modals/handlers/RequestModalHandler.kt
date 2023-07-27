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
package me.xezard.devmc.drazex.discord.service.modals.handlers

import discord4j.common.util.Snowflake
import discord4j.core.event.domain.interaction.ModalSubmitInteractionEvent
import discord4j.core.`object`.component.ActionRow
import discord4j.core.`object`.component.Button
import discord4j.core.`object`.component.TextInput
import discord4j.core.spec.EmbedCreateSpec
import discord4j.discordjson.json.MessageCreateRequest
import me.xezard.devmc.drazex.discord.config.DiscordConfiguration
import me.xezard.devmc.drazex.discord.config.modals.properties.ModalProperties
import me.xezard.devmc.drazex.discord.service.app.DiscordService.Companion.DISCORD_USER_URL
import me.xezard.devmc.drazex.discord.service.buttons.handlers.RequestDeleteButtonHandler
import me.xezard.devmc.drazex.discord.service.messages.MessagesService
import me.xezard.devmc.drazex.discord.service.modals.AbstractModalHandler
import me.xezard.devmc.drazex.discord.service.modals.ModalsService
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

abstract class RequestModalHandler (
    private val modalsService: ModalsService,
    private val messagesService: MessagesService,
    private val discordConfiguration: DiscordConfiguration,
    private val properties: ModalProperties
) : AbstractModalHandler(modalsService, properties) {
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
        val embedBuilder = EmbedCreateSpec.builder()
            .author(member.displayName, DISCORD_USER_URL.replace(ID_REPLACE_PLACEHOLDER, id), member.avatarUrl)
            .color(this.messagesService.getColorFromString(this.discordConfiguration.requestsMessageColor))

        this.getInputsMapping(this.properties).forEach { (field, inputId) ->
            this.modalsService.getInputValue(inputs, inputId)?.let {
                if (field == "Тип услуги:") {
                    embedBuilder.title("$title: $it")
                } else {
                    embedBuilder.addField(field, it, false)
                }
            }
        }

        embedBuilder.addField(EMBED_PUBLISHED_BY_FIELD, member.mention, false)

        val embed = embedBuilder.build().asRequest()
        val actionRow = ActionRow.of(Button.secondary(
            RequestDeleteButtonHandler.BUTTON_ID + id,
                DELETE_BUTTON_LABEL
        ))
        val message = MessageCreateRequest.builder()
            .embed(embed)
            .build()
            .withComponents(actionRow.data)
        val channels = event.interaction.guild.flatMapMany {
            Flux.fromIterable(channelsIds.map(Snowflake::of)).flatMap(it::getChannelById)
        }
        val messages = channels.flatMap { it.restChannel.createMessage(message) }
        val reply = event.reply(REQUEST_SUCCESSFULLY_PUBLISHED_MESSAGE).withEphemeral(true)

        return messages.then(reply)
    }

    private fun getInputsMapping(properties: ModalProperties) =
        properties.inputs.map { (name, data) -> data.description to "${properties.id}-$name" }.toMap()
}