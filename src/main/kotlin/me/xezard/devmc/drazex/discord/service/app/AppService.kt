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