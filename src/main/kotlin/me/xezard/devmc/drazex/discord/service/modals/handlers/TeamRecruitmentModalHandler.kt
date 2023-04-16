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
import me.xezard.devmc.drazex.discord.config.properties.TeamRequestChannelsProperties
import me.xezard.devmc.drazex.discord.service.app.DiscordService
import me.xezard.devmc.drazex.discord.service.messages.MessagesService
import me.xezard.devmc.drazex.discord.service.modals.IModalHandler
import me.xezard.devmc.drazex.discord.service.modals.ModalsService
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class TeamRecruitmentModalHandler (
    private val modalsService: ModalsService,
    private val messagesService: MessagesService,
    private val discordConfiguration: DiscordConfiguration,
    private val teamRequestChannelsProperties: TeamRequestChannelsProperties
): IModalHandler {
    companion object {
        private const val MODAL_TITLE = "Набор команды"
        private const val MODAL_ID = "team-recruitment-modal"
        private const val TEAM_RECRUITMENT_DESCRIPTION_INPUT_ID = "team-recruitment-description-input"
        private const val TEAM_RECRUITMENT_AGE_INPUT_ID = "team-recruitment-age-input"
        private const val TEAM_RECRUITMENT_VACANCIES_INPUT_ID = "team-recruitment-vacancies-input"
        private const val TEAM_RECRUITMENT_CONTACTS_INPUT_ID = "team-recruitment-contacts-input"
        private const val TEAM_RECRUITMENT_DESCRIPTION_INPUT_DESCRIPTION = "Описание"
        private const val TEAM_RECRUITMENT_AGE_INPUT_DESCRIPTION = "Возраст"
        private const val TEAM_RECRUITMENT_VACANCIES_INPUT_DESCRIPTION = "Описание вакансий"
        private const val TEAM_RECRUITMENT_CONTACTS_INPUT_DESCRIPTION = "Контакты"
        private const val TEAM_RECRUITMENT_AGE_INPUT_PLACEHOLDER = "от 16 лет"

        private const val TEAM_RECRUITMENT_DESCRIPTION_INPUT_MINIMUM_LENGTH = 10
        private const val TEAM_RECRUITMENT_AGE_INPUT_MINIMUM_LENGTH = 2
        private const val TEAM_RECRUITMENT_VACANCIES_INPUT_MINIMUM_LENGTH = 10
        private const val TEAM_RECRUITMENT_CONTACTS_INPUT_MINIMUM_LENGTH = 10
        private const val TEAM_RECRUITMENT_DESCRIPTION_INPUT_MAXIMUM_LENGTH = 1024
        private const val TEAM_RECRUITMENT_AGE_INPUT_MAXIMUM_LENGTH = 10
        private const val TEAM_RECRUITMENT_VACANCIES_INPUT_MAXIMUM_LENGTH = 1024
        private const val TEAM_RECRUITMENT_CONTACTS_INPUT_MAXIMUM_LENGTH = 256
    }

    override fun handle(event: ModalSubmitInteractionEvent): Mono<Void> {
        val member = event.interaction.member.orElse(null)
        val fullName = "${member?.displayName}#${member?.discriminator}"
        val mention = member?.mention ?: ""
        val avatar = member?.avatarUrl ?: ""
        val id = member?.id?.asString() ?: ""

        val inputs = event.getComponents(TextInput::class.java)

        val description = this.modalsService.getInputValue(inputs, TEAM_RECRUITMENT_DESCRIPTION_INPUT_ID)
        val age = this.modalsService.getInputValue(inputs, TEAM_RECRUITMENT_AGE_INPUT_ID)
        val vacancies = this.modalsService.getInputValue(inputs, TEAM_RECRUITMENT_VACANCIES_INPUT_ID)
        val contacts = this.modalsService.getInputValue(inputs, TEAM_RECRUITMENT_CONTACTS_INPUT_ID)

        val embed = EmbedCreateSpec.builder()
                .author(fullName, DiscordService.DISCORD_USER_URL.replace("{id}", id), avatar)
                .color(this.messagesService.getColorFromString(this.discordConfiguration.requestsMessageColor))
                .addField("Описание:", description, false)
                .addField("Возраст:", age, false)
                .addField("Вакансии:", vacancies, false)
                .addField("Контакты:", contacts, false)
                .addField("Опубликовал:", mention, false)
                .build()
                .asRequest()

        val deleteButton = Button.secondary(id, "❌")
        val actionRow = ActionRow.of(deleteButton)

        val message = MessageCreateRequest.builder()
                .embed(embed)
                .build()
                .withComponents(actionRow.data)

        val channelIds = this.teamRequestChannelsProperties.recruitment.map(Snowflake::of)
        val channels = event.interaction.guild.flatMapMany { guild ->
            Flux.fromIterable(channelIds).flatMap(guild::getChannelById)
        }

        val messages = channels.flatMap { it.restChannel.createMessage(message) }
        val reply = event.reply("Ваш запрос успешно опубликован!")
                .withEphemeral(true)

        return messages.then(reply)
    }

    override fun create(): InteractionPresentModalSpec {
        val descriptionInput = TextInput.small(TEAM_RECRUITMENT_DESCRIPTION_INPUT_ID, TEAM_RECRUITMENT_DESCRIPTION_INPUT_DESCRIPTION,
                TEAM_RECRUITMENT_DESCRIPTION_INPUT_MINIMUM_LENGTH, TEAM_RECRUITMENT_DESCRIPTION_INPUT_MAXIMUM_LENGTH)
                .required()

        val ageInput = TextInput.small(TEAM_RECRUITMENT_AGE_INPUT_ID, TEAM_RECRUITMENT_AGE_INPUT_DESCRIPTION,
                TEAM_RECRUITMENT_AGE_INPUT_MINIMUM_LENGTH, TEAM_RECRUITMENT_AGE_INPUT_MAXIMUM_LENGTH)
                .placeholder(TEAM_RECRUITMENT_AGE_INPUT_PLACEHOLDER)
                .required()

        val vacancies = TextInput.small(TEAM_RECRUITMENT_VACANCIES_INPUT_ID, TEAM_RECRUITMENT_VACANCIES_INPUT_DESCRIPTION,
                TEAM_RECRUITMENT_VACANCIES_INPUT_MINIMUM_LENGTH, TEAM_RECRUITMENT_VACANCIES_INPUT_MAXIMUM_LENGTH)
                .required()

        val contactsInput = TextInput.small(TEAM_RECRUITMENT_CONTACTS_INPUT_ID, TEAM_RECRUITMENT_CONTACTS_INPUT_DESCRIPTION,
                TEAM_RECRUITMENT_CONTACTS_INPUT_MINIMUM_LENGTH, TEAM_RECRUITMENT_CONTACTS_INPUT_MAXIMUM_LENGTH)
                .required()

        return InteractionPresentModalSpec.builder()
                .title(MODAL_TITLE)
                .customId(MODAL_ID)
                .addComponent(ActionRow.of(descriptionInput))
                .addComponent(ActionRow.of(ageInput))
                .addComponent(ActionRow.of(vacancies))
                .addComponent(ActionRow.of(contactsInput))
                .build()
    }

    override fun id(): String {
        return MODAL_ID
    }
}