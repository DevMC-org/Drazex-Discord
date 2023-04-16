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
import discord4j.core.spec.InteractionPresentModalSpec
import discord4j.discordjson.json.MessageCreateRequest
import me.xezard.devmc.drazex.discord.config.DiscordConfiguration
import me.xezard.devmc.drazex.discord.config.properties.DevelopmentRequestChannelsProperties
import me.xezard.devmc.drazex.discord.service.app.DiscordService.Companion.DISCORD_USER_URL
import me.xezard.devmc.drazex.discord.service.messages.MessagesService
import me.xezard.devmc.drazex.discord.service.modals.IModalHandler
import me.xezard.devmc.drazex.discord.service.modals.ModalsService
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class ExecutorSearchModalHandler (
    private val discordConfiguration: DiscordConfiguration,
    private val messagesService: MessagesService,
    private val modalsService: ModalsService,
    private val developmentRequestChannelsProperties: DevelopmentRequestChannelsProperties
): IModalHandler {
    companion object {
        private const val SERVICE_TYPE_OPTION_DESCRIPTION = "Тип услуги"
        private const val MODAL_TITLE = "Поиск исполнителя"
        private const val MODAL_ID = "create-request-modal"
        private const val SERVICE_TYPE_INPUT_ID = "service-type-input"
        private const val SERVICE_TYPE_INPUT_PLACEHOLDER = "Разработка плагина"
        private const val SERVICE_DESCRIPTION_INPUT_ID = "service-description-input"
        private const val SERVICE_DESCRIPTION_INPUT_DESCRIPTION = "Описание запроса"
        private const val SERVICE_BUDGET_INPUT_ID = "service-budget-input"
        private const val SERVICE_BUDGET_INPUT_DESCRIPTION = "Бюджет"
        private const val SERVICE_BUDGET_INPUT_PLACEHOLDER = "100€"

        private const val SERVICE_TYPE_INPUT_MINIMUM_LENGTH = 5
        private const val SERVICE_TYPE_INPUT_MAXIMUM_LENGTH = 100
        private const val SERVICE_DESCRIPTION_INPUT_MINIMUM_LENGTH = 10
        private const val SERVICE_DESCRIPTION_INPUT_MAXIMUM_LENGTH = 1024
        private const val SERVICE_BUDGET_INPUT_MINIMUM_LENGTH = 3
        private const val SERVICE_BUDGET_INPUT_MAXIMUM_LENGTH = 25
    }

    override fun handle(event: ModalSubmitInteractionEvent): Mono<Void> {
        val member = event.interaction.member.orElse(null)
        val fullName = "${member?.displayName}#${member?.discriminator}"
        val mention = member?.mention ?: ""
        val avatar = member?.avatarUrl ?: ""
        val id = member?.id?.asString() ?: ""

        val inputs = event.getComponents(TextInput::class.java)

        val serviceType = this.modalsService.getInputValue(inputs, SERVICE_TYPE_INPUT_ID)
        val description = this.modalsService.getInputValue(inputs, SERVICE_DESCRIPTION_INPUT_ID)
        val budget = this.modalsService.getInputValue(inputs, SERVICE_BUDGET_INPUT_ID)

        val embed = EmbedCreateSpec.builder()
                .author(fullName, DISCORD_USER_URL.replace("{id}", id), avatar)
                .title("Запрос: $serviceType")
                .color(this.messagesService.getColorFromString(this.discordConfiguration.requestsMessageColor))
                .description(description)
                .addField("Бюджет:", budget, false)
                .addField("Опубликовал:", mention, false)
                .build()
                .asRequest()

        val deleteButton = Button.secondary(id, "❌")
        val actionRow = ActionRow.of(deleteButton)

        val message = MessageCreateRequest.builder()
                .embed(embed)
                .build()
                .withComponents(actionRow.data)

        val channelIds = this.developmentRequestChannelsProperties.development.map(Snowflake::of)
        val channels = event.interaction.guild.flatMapMany { guild ->
            Flux.fromIterable(channelIds).flatMap(guild::getChannelById)
        }

        val messages = channels.flatMap { it.restChannel.createMessage(message) }
        val reply = event.reply("Ваш запрос успешно опубликован!")
                .withEphemeral(true)

        return messages.then(reply)
    }

    override fun create(): InteractionPresentModalSpec {
        val serviceTypeInput = TextInput.small(SERVICE_TYPE_INPUT_ID, SERVICE_TYPE_OPTION_DESCRIPTION,
                SERVICE_TYPE_INPUT_MINIMUM_LENGTH, SERVICE_TYPE_INPUT_MAXIMUM_LENGTH)
                .placeholder(SERVICE_TYPE_INPUT_PLACEHOLDER)
                .required()

        val descriptionInput = TextInput.small(SERVICE_DESCRIPTION_INPUT_ID, SERVICE_DESCRIPTION_INPUT_DESCRIPTION,
                SERVICE_DESCRIPTION_INPUT_MINIMUM_LENGTH, SERVICE_DESCRIPTION_INPUT_MAXIMUM_LENGTH)
                .required()

        val budgetInput = TextInput.small(SERVICE_BUDGET_INPUT_ID, SERVICE_BUDGET_INPUT_DESCRIPTION,
                SERVICE_BUDGET_INPUT_MINIMUM_LENGTH, SERVICE_BUDGET_INPUT_MAXIMUM_LENGTH)
                .placeholder(SERVICE_BUDGET_INPUT_PLACEHOLDER)
                .required()

        return InteractionPresentModalSpec.builder()
                .title(MODAL_TITLE)
                .customId(MODAL_ID)
                .addComponent(ActionRow.of(serviceTypeInput))
                .addComponent(ActionRow.of(descriptionInput))
                .addComponent(ActionRow.of(budgetInput))
                .build()
    }

    override fun id(): String {
        return MODAL_ID
    }
}