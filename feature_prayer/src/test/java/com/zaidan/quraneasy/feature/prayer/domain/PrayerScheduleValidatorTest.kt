package com.zaidan.quraneasy.feature.prayer.domain

import org.junit.Assert.assertSame
import org.junit.Test

class PrayerScheduleValidatorTest {
    @Test
    fun `accepts normalized six-prayer schedule`() {
        val schedule = PrayerSchedule(
            date = "27-07-2026",
            locationKey = "28.614,77.209",
            source = "test",
            prayers = listOf(
                PrayerTime("Fajr", "04:11"),
                PrayerTime("Sunrise", "05:37"),
                PrayerTime("Dhuhr", "12:28"),
                PrayerTime("Asr", "17:08"),
                PrayerTime("Maghrib", "19:17"),
                PrayerTime("Isha", "20:43")
            )
        )

        assertSame(schedule, PrayerScheduleValidator.requireValid(schedule))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `rejects provider response with invalid time`() {
        PrayerScheduleValidator.requireValid(
            PrayerSchedule(
                date = "27-07-2026",
                locationKey = "28.614,77.209",
                source = "test",
                prayers = listOf(
                    PrayerTime("Fajr", "not-a-time"),
                    PrayerTime("Sunrise", "05:37"),
                    PrayerTime("Dhuhr", "12:28"),
                    PrayerTime("Asr", "17:08"),
                    PrayerTime("Maghrib", "19:17"),
                    PrayerTime("Isha", "20:43")
                )
            )
        )
    }
}
