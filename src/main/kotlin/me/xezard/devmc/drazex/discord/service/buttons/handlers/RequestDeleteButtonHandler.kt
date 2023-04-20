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
import me.xezard.devmc.drazex.discord.config.properties.RolesProperties
import me.xezard.devmc.drazex.discord.service.buttons.IButtonHandler
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class RequestDeleteButtonHandler (
    private val rolesProperties: RolesProperties
): IButtonHandler {
    companion object {
        const val BUTTON_ID = "request-button:"
    }

    override fun handle(event: ButtonInteractionEvent, buttonId: String): Mono<Void> {
        val member = event.interaction.member
        val userId = event.interaction.user.id

        val hasPermission = Mono.zip(Mono.justOrEmpty(member).flatMap {
            it.roles.any { role -> role.id.asString() == rolesProperties.admin }
        }, Mono.just(userId.asString() == buttonId)) { permission, owner -> permission || owner }

        return hasPermission.flatMap {
            if (it) {
                Mono.justOrEmpty(event.message).flatMap {
                    message -> message.delete()
                }
            } else {
                event.reply("Вы не можете удалить запрос, созданный другим пользователем.")
                        .withEphemeral(true)
            }
        }
    }

    override fun tracks(id: String): Boolean {
        return id.startsWith(BUTTON_ID)
    }
}