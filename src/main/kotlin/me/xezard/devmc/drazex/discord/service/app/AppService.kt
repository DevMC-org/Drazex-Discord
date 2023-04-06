/*
 * Drazex-Discord
 * Discord-bot for the project community devmc.org,
 * designed to automate administrative tasks, notifications
 * and other functionality related to the functioning of the community
 * Copyright (C) 2023 Ivan `Xezard` Zotov
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package me.xezard.devmc.drazex.discord.service.app

import org.springframework.stereotype.Service
import java.lang.management.ManagementFactory

@Service
class AppService (
    private val timeService: TimeService
) {
    val replaces: Map<String, Any> by lazy {
        mapOf(
            "{uptime}" to getUptime(),
            "{used_memory}" to getUsedMemory(),
            "{maximum_memory}" to getMaximumMemory()
        )
    }

    private fun getUptime(): String {
        val uptime = ManagementFactory.getRuntimeMXBean().uptime

        return timeService.formatTime(uptime)
    }

    private fun getUsedMemory(): Long {
        val totalMemory = Runtime.getRuntime().totalMemory()
        val freeMemory = Runtime.getRuntime().freeMemory()

        return (totalMemory - freeMemory) / 1024 / 1024
    }

    private fun getMaximumMemory(): Long {
        val maxMemory = Runtime.getRuntime().maxMemory()
        val usedMemory = getUsedMemory() * 1024 * 1024

        return (maxMemory - usedMemory) / 1024 / 1024
    }
}