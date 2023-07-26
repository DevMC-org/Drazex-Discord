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
package me.xezard.devmc.drazex.discord.service.buttons.handlers

import discord4j.core.event.domain.interaction.ButtonInteractionEvent
import discord4j.core.`object`.entity.Message
import me.xezard.devmc.drazex.discord.config.properties.RolesProperties
import me.xezard.devmc.drazex.discord.service.buttons.IButtonHandler
import me.xezard.devmc.drazex.discord.service.roles.RolesService
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class RequestDeleteButtonHandler (
    private val rolesService: RolesService,
    private val rolesProperties: RolesProperties
): IButtonHandler {
    companion object {
        private const val YOU_CANT_DELETE_OTHERS_REQUESTS_ERROR =
            "Вы не можете удалить запрос, созданный другим пользователем."

        const val BUTTON_ID = "request-button:"
    }

    override fun handle(event: ButtonInteractionEvent, buttonId: String): Mono<Void> {
        val member = event.interaction.member.orElse(null) ?: return Mono.empty()
        val userId = event.interaction.user.id
        val isUserButton = Mono.just(userId.asString() == buttonId)
        val hasPermission = this.rolesService.hasRole(member, this.rolesProperties.admin)
        val hasAccess = Mono.zip(hasPermission, isUserButton) { permission, owner -> permission || owner }

        return hasAccess.flatMap {
            when (it) {
                true -> Mono.justOrEmpty(event.message).flatMap(Message::delete)
                false -> event.reply(YOU_CANT_DELETE_OTHERS_REQUESTS_ERROR).withEphemeral(true)
            }
        }
    }

    override fun tracks(id: String): Boolean =
        id.startsWith(BUTTON_ID)
}