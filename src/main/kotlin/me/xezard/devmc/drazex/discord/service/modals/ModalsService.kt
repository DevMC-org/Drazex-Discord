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
package me.xezard.devmc.drazex.discord.service.modals

import discord4j.core.`object`.component.Button
import discord4j.core.`object`.component.TextInput
import me.xezard.devmc.drazex.discord.service.buttons.handlers.RequestDeleteButtonHandler
import org.springframework.stereotype.Service

@Service
class ModalsService {
    companion object {
        private const val DELETE_BUTTON_LABEL = "❌"
    }

    fun createDeleteButton(id: String): Button {
        return Button.secondary(RequestDeleteButtonHandler.BUTTON_ID + id, DELETE_BUTTON_LABEL)
    }

    fun getInputValue(inputs: List<TextInput>, id: String): String {
        return inputs.find { it.customId == id }?.data?.value()?.get() ?: ""
    }
}