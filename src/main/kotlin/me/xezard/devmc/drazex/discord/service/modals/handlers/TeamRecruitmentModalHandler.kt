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

import discord4j.core.event.domain.interaction.ModalSubmitInteractionEvent
import discord4j.core.`object`.component.ActionRow
import discord4j.core.`object`.component.TextInput
import discord4j.core.spec.InteractionPresentModalSpec
import me.xezard.devmc.drazex.discord.config.DiscordConfiguration
import me.xezard.devmc.drazex.discord.config.properties.TeamRequestChannelsProperties
import me.xezard.devmc.drazex.discord.service.messages.MessagesService
import me.xezard.devmc.drazex.discord.service.modals.ModalsService
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class TeamRecruitmentModalHandler (
    modalsService: ModalsService,
    messagesService: MessagesService,
    discordConfiguration: DiscordConfiguration,
    private val teamRequestChannelsProperties: TeamRequestChannelsProperties
): RequestModalHandler(modalsService, messagesService, discordConfiguration) {
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

        private val INPUTS_MAPPING = mapOf(
            EMBED_DESCRIPTION_FIELD to TEAM_RECRUITMENT_DESCRIPTION_INPUT_ID,
            EMBED_AGE_FIELD to TEAM_RECRUITMENT_AGE_INPUT_ID,
            EMBED_VACANCIES_FIELD to TEAM_RECRUITMENT_VACANCIES_INPUT_ID,
            EMBED_CONTACTS_FIELD to TEAM_RECRUITMENT_CONTACTS_INPUT_ID
        )
    }

    override val id
        get() = MODAL_ID

    override fun handle(event: ModalSubmitInteractionEvent): Mono<Void> {
        return this.handle(event, INPUTS_MAPPING, this.teamRequestChannelsProperties.recruitment)
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
}