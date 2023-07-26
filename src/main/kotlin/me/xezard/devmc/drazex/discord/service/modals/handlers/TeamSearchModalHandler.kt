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
class TeamSearchModalHandler (
    modalsService: ModalsService,
    messagesService: MessagesService,
    discordConfiguration: DiscordConfiguration,
    private val teamRequestChannelsProperties: TeamRequestChannelsProperties
): RequestModalHandler(modalsService, messagesService, discordConfiguration) {
    companion object {
        private const val MODAL_TITLE = "Поиск команды"
        private const val MODAL_ID = "team-search-modal"
        private const val TEAM_SEARCH_DESCRIPTION_INPUT_ID = "team-search-description-input"
        private const val TEAM_SEARCH_AGE_INPUT_ID = "team-search-age-input"
        private const val TEAM_SEARCH_CONDITIONS_INPUT_ID = "team-search-salary-input"
        private const val TEAM_SEARCH_CONTACTS_INPUT_ID = "team-search-contacts-input"
        private const val TEAM_SEARCH_DESCRIPTION_INPUT_DESCRIPTION = "Описание"
        private const val TEAM_SEARCH_AGE_INPUT_DESCRIPTION = "Возраст"
        private const val TEAM_SEARCH_CONDITIONS_INPUT_DESCRIPTION = "Условия"
        private const val TEAM_SEARCH_CONTACTS_INPUT_DESCRIPTION = "Контакты"
        private const val TEAM_SEARCH_AGE_INPUT_PLACEHOLDER = "от 16 лет"
        private const val TEAM_SEARCH_CONDITIONS_INPUT_PLACEHOLDER = "15к в месяц"

        private const val TEAM_SEARCH_DESCRIPTION_INPUT_MINIMUM_LENGTH = 10
        private const val TEAM_SEARCH_AGE_INPUT_MINIMUM_LENGTH = 2
        private const val TEAM_SEARCH_CONDITIONS_INPUT_MINIMUM_LENGTH = 10
        private const val TEAM_SEARCH_CONTACTS_INPUT_MINIMUM_LENGTH = 10
        private const val TEAM_SEARCH_DESCRIPTION_INPUT_MAXIMUM_LENGTH = 1024
        private const val TEAM_SEARCH_AGE_INPUT_MAXIMUM_LENGTH = 10
        private const val TEAM_SEARCH_CONDITIONS_INPUT_MAXIMUM_LENGTH = 512
        private const val TEAM_SEARCH_CONTACTS_INPUT_MAXIMUM_LENGTH = 256

        private val INPUTS_MAPPING = mapOf(
            EMBED_DESCRIPTION_FIELD to TEAM_SEARCH_DESCRIPTION_INPUT_ID,
            EMBED_AGE_FIELD to TEAM_SEARCH_AGE_INPUT_ID,
            EMBED_CONDITIONS_FIELD to TEAM_SEARCH_CONDITIONS_INPUT_ID,
            EMBED_CONTACTS_FIELD to TEAM_SEARCH_CONTACTS_INPUT_ID
        )
    }

    override fun handle(event: ModalSubmitInteractionEvent): Mono<Void> {
        return this.handle(event, INPUTS_MAPPING, this.teamRequestChannelsProperties.recruitment)
    }

    override fun create(): InteractionPresentModalSpec {
        val descriptionInput = TextInput.small(TEAM_SEARCH_DESCRIPTION_INPUT_ID, TEAM_SEARCH_DESCRIPTION_INPUT_DESCRIPTION,
                TEAM_SEARCH_DESCRIPTION_INPUT_MINIMUM_LENGTH, TEAM_SEARCH_DESCRIPTION_INPUT_MAXIMUM_LENGTH)
                .required()

        val ageInput = TextInput.small(TEAM_SEARCH_AGE_INPUT_ID, TEAM_SEARCH_AGE_INPUT_DESCRIPTION,
                TEAM_SEARCH_AGE_INPUT_MINIMUM_LENGTH, TEAM_SEARCH_AGE_INPUT_MAXIMUM_LENGTH)
                .placeholder(TEAM_SEARCH_AGE_INPUT_PLACEHOLDER)
                .required()

        val conditionsInput = TextInput.small(TEAM_SEARCH_CONDITIONS_INPUT_ID, TEAM_SEARCH_CONDITIONS_INPUT_DESCRIPTION,
                TEAM_SEARCH_CONDITIONS_INPUT_MINIMUM_LENGTH, TEAM_SEARCH_CONDITIONS_INPUT_MAXIMUM_LENGTH)
                .placeholder(TEAM_SEARCH_CONDITIONS_INPUT_PLACEHOLDER)
                .required()

        val contactsInput = TextInput.small(TEAM_SEARCH_CONTACTS_INPUT_ID, TEAM_SEARCH_CONTACTS_INPUT_DESCRIPTION,
                TEAM_SEARCH_CONTACTS_INPUT_MINIMUM_LENGTH, TEAM_SEARCH_CONTACTS_INPUT_MAXIMUM_LENGTH)
                .required()

        return InteractionPresentModalSpec.builder()
                .title(MODAL_TITLE)
                .customId(MODAL_ID)
                .addComponent(ActionRow.of(descriptionInput))
                .addComponent(ActionRow.of(ageInput))
                .addComponent(ActionRow.of(conditionsInput))
                .addComponent(ActionRow.of(contactsInput))
                .build()
    }

    override fun id(): String {
        return MODAL_ID
    }
}