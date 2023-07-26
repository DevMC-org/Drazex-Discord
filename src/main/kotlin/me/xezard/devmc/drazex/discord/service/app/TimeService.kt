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
import java.time.Duration

@Service
class TimeService {
    companion object {
        private val TIME_UNITS = listOf(
            "год" to "года" to "лет",
            "месяц" to "месяца" to "месяцев",
            "день" to "дня" to "дней",
            "час" to "часа" to "часов",
            "минута" to "минуты" to "минут",
            "секунда" to "секунды" to "секунд"
        )

        private const val TIME_VALUES_SEPARATOR = ","
        private const val TIME_VALUES_LAST_SEPARATOR = " и"

        private const val DAYS_IN_A_YEAR = 365
        private const val MONTHS_PER_YEAR = 12
        private const val DAYS_IN_A_MONTH = 30
        private const val HOURS_IN_A_DAY = 24
        private const val MINUTES_IN_A_HOUR = 60
        private const val SECONDS_IN_A_MINUTE = 60
    }

    fun formatTime(timeMillis: Long): String {
        val duration = Duration.ofMillis(timeMillis)
        val years = duration.toDays() / DAYS_IN_A_YEAR
        val months = duration.toDays() / DAYS_IN_A_MONTH % MONTHS_PER_YEAR
        val days = duration.toDays() % DAYS_IN_A_MONTH
        val hours = duration.toHours() % HOURS_IN_A_DAY
        val minutes = duration.toMinutes() % MINUTES_IN_A_HOUR
        val seconds = duration.seconds % SECONDS_IN_A_MINUTE
        val timeUnits = listOf(
            years to TIME_UNITS[0],
            months to TIME_UNITS[1],
            days to TIME_UNITS[2],
            hours to TIME_UNITS[3],
            minutes to TIME_UNITS[4],
            seconds to TIME_UNITS[5]
        )

        return buildString {
            timeUnits.filter { it.first > 0 }
                    .joinTo(this, "$TIME_VALUES_SEPARATOR ") { "${it.first} ${getCorrectForm(
                            it.first, 
                            it.second.first.first, 
                            it.second.first.second, 
                            it.second.second
                    )}" }
                    .replaceLast(TIME_VALUES_SEPARATOR, TIME_VALUES_LAST_SEPARATOR)
        }
    }

    private fun getCorrectForm(value: Long, oneForm: String, twoForm: String, manyForm: String): String {
        val lastDigit = value % 10
        val lastTwoDigits = value % 100

        return when {
            lastDigit == 1L && lastTwoDigits != 11L -> oneForm
            lastDigit in 2..4 && (lastTwoDigits < 10 || lastTwoDigits > 20) -> twoForm
            else -> manyForm
        }
    }

    private fun StringBuilder.replaceLast(oldValue: String, newValue: String): String {
        val lastIndexOf = this.lastIndexOf(oldValue)

        return if (lastIndexOf == -1) this.toString() else {
            this.replace(lastIndexOf, lastIndexOf + oldValue.length, newValue).toString()
        }
    }
}