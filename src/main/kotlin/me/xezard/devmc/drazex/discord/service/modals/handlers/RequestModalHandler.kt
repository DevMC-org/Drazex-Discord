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
import discord4j.core.`object`.component.TextInput
import discord4j.core.spec.EmbedCreateSpec
import discord4j.discordjson.json.MessageCreateRequest
import me.xezard.devmc.drazex.discord.config.DiscordConfiguration
import me.xezard.devmc.drazex.discord.service.app.DiscordService.Companion.DISCORD_USER_URL
import me.xezard.devmc.drazex.discord.service.messages.MessagesService
import me.xezard.devmc.drazex.discord.service.modals.ModalHandler
import me.xezard.devmc.drazex.discord.service.modals.ModalsService
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

abstract class RequestModalHandler (
    private val modalsService: ModalsService,
    private val messagesService: MessagesService,
    private val discordConfiguration: DiscordConfiguration,
) : ModalHandler {
    companion object {
        private const val ID_REPLACE_PLACEHOLDER = "{id}"

        private const val EMBED_PUBLISHED_BY_FIELD = "Опубликовал:"

        const val EMBED_DESCRIPTION_FIELD = "Описание:"
        const val EMBED_AGE_FIELD = "Возраст:"
        const val EMBED_VACANCIES_FIELD = "Вакансии:"
        const val EMBED_BUDGET_FIELD = "Бюджет:"
        const val EMBED_CONDITIONS_FIELD = "Условия:"
        const val EMBED_CONTACTS_FIELD = "Контакты:"

        private const val REQUEST_SUCCESSFULLY_PUBLISHED_MESSAGE = "Ваш запрос успешно опубликован!"
    }

    fun handle(
        event: ModalSubmitInteractionEvent,
        inputsMapping: Map<String, String>,
        channelsIds: List<String>,
        title: String? = null
    ): Mono<Void> {
        val member = event.interaction.member.orElse(null) ?: return Mono.empty()
        val fullName = "${member.displayName}#${member.discriminator}"
        val mention = member.mention
        val avatar = member.avatarUrl
        val id = member.id.asString()
        val inputs = event.getComponents(TextInput::class.java)
        val embedBuilder = EmbedCreateSpec.builder()
            .author(fullName, DISCORD_USER_URL.replace(ID_REPLACE_PLACEHOLDER, id), avatar)
            .color(this.messagesService.getColorFromString(this.discordConfiguration.requestsMessageColor))

        inputsMapping.forEach { (field, inputId) ->
            val value = this.modalsService.getInputValue(inputs, inputId)

            embedBuilder.addField(field, value, false)

            if (field == title) {
                embedBuilder.title("$field: $value")
            }
        }

        embedBuilder.addField(EMBED_PUBLISHED_BY_FIELD, mention, false)

        val embed = embedBuilder.build().asRequest()
        val actionRow = ActionRow.of(this.modalsService.createDeleteButton(id))
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
}