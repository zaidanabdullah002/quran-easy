package com.zaidan.quraneasy.feature.prayer.domain

data class PrayerSchedule(
    val date: String,
    val locationKey: String,
    val prayers: List<PrayerTime>,
    val source: String
)

data class PrayerTime(
    val name: String,
    val time: String
)

object PrayerScheduleValidator {
    private val requiredNames = listOf("Fajr", "Sunrise", "Dhuhr", "Asr", "Maghrib", "Isha")
    private val timePattern = Regex("""^(?:[01]\d|2[0-3]):[0-5]\d$""")

    fun requireValid(schedule: PrayerSchedule): PrayerSchedule {
        require(schedule.prayers.map { it.name } == requiredNames) {
            "Prayer schedule does not contain the required prayers in order"
        }
        require(schedule.prayers.all { timePattern.matches(it.time) }) {
            "Prayer schedule contains an invalid time"
        }
        return schedule
    }
}
