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
package me.xezard.devmc.drazex.discord.service.app

import org.springframework.stereotype.Service
import org.springframework.util.unit.DataSize
import org.springframework.util.unit.DataUnit
import java.lang.management.ManagementFactory

@Service
class AppService (
    private val timeService: TimeService
) {
    companion object {
        const val UPTIME_REPLACE_PLACEHOLDER = "{uptime}"
        const val USED_MEMORY_REPLACE_PLACEHOLDER = "{used_memory}"
        const val AVAILABLE_MEMORY_REPLACE_PLACEHOLDER = "{available_memory}"
        const val MAXIMUM_MEMORY_REPLACE_PLACEHOLDER = "{maximum_memory}"

        val RUNTIME: Runtime = Runtime.getRuntime()
    }

    val replaces: () -> Map<String, String> = {
        mapOf(
            UPTIME_REPLACE_PLACEHOLDER to this.getUptime(),
            USED_MEMORY_REPLACE_PLACEHOLDER to this.getUsedMemory().toMegabytes().toString(),
            AVAILABLE_MEMORY_REPLACE_PLACEHOLDER to this.getAvailableMemory().toMegabytes().toString(),
            MAXIMUM_MEMORY_REPLACE_PLACEHOLDER to this.getMaximumMemory().toMegabytes().toString(),
        )
    }

    private fun getUptime(): String {
        val uptime = ManagementFactory.getRuntimeMXBean().uptime

        return this.timeService.formatTime(uptime)
    }

    private fun getUsedMemory(): DataSize {
        val totalMemory = RUNTIME.totalMemory()
        val freeMemory = RUNTIME.freeMemory()

        return DataSize.of(totalMemory - freeMemory, DataUnit.BYTES)
    }

    private fun getAvailableMemory(): DataSize {
        val maxMemory = RUNTIME.maxMemory()
        val usedMemory = this.getUsedMemory()

        return DataSize.of(maxMemory - usedMemory.toBytes(), DataUnit.BYTES)
    }

    private fun getMaximumMemory(): DataSize {
        return DataSize.of(RUNTIME.maxMemory(), DataUnit.BYTES)
    }
}