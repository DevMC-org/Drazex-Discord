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
    }

    fun formatTime(timeMillis: Long): String {
        val duration = Duration.ofMillis(timeMillis)
        val years = duration.toDays() / 365
        val months = duration.toDays() / 30 % 12
        val days = duration.toDays() % 30
        val hours = duration.toHours() % 24
        val minutes = duration.toMinutes() % 60
        val seconds = duration.seconds % 60

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
                    .joinTo(this, ", ") { "${it.first} ${getCorrectForm(
                            it.first, 
                            it.second.first.first, 
                            it.second.first.second, 
                            it.second.second
                    )}" }
                    .replaceLast(",", " и")
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