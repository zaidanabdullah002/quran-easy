package com.zaidan.quraneasy.feature.prayer.presentation

import java.util.Calendar
import org.junit.Assert.*
import org.junit.Test

class PrayerTimeProgressTest {
    private val prayers = listOf("Fajr" to "05:00", "Dhuhr" to "12:00", "Asr" to "15:00", "Maghrib" to "18:00", "Isha" to "20:00")
        .map { PrayerRowUi(it.first, it.second, false, true) }
    private fun at(hour: Int, minute: Int = 0) = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour); set(Calendar.MINUTE, minute)
    }
    @Test fun countdownLabelsUpcomingPrayer() {
        val result = calculateCurrentPrayerProgress(prayers, at(14, 30))!!
        assertEquals("Asr", result.name)
        assertEquals("30m", result.timeLeftLabel)
    }
    @Test fun midnightWrapAndPreDawnAreConsistent() {
        assertEquals("7h 0m", calculateCurrentPrayerProgress(prayers, at(22))!!.timeLeftLabel)
        assertEquals("Fajr", calculateCurrentPrayerProgress(prayers, at(22))!!.name)
        assertEquals("1h 0m", calculateCurrentPrayerProgress(prayers, at(4))!!.timeLeftLabel)
    }
    @Test fun exactPrayerBoundaryMovesToNextPrayer() {
        assertEquals("Dhuhr", calculateCurrentPrayerProgress(prayers, at(5))!!.name)
    }
    @Test fun invalidTimesDoNotProduceMisleadingCountdowns() {
        assertNull(calculateCurrentPrayerProgress(listOf(PrayerRowUi("Fajr", "25:90", false, true)), at(4)))
        assertNull(calculateCurrentPrayerProgress(PrayerRowUi.placeholderList(), at(4)))
    }
    @Test fun clockOrderingHandlesSchedulesAcrossMidnight() {
        assertEquals("Asr", calculateCurrentPrayerProgress(prayers.reversed(), at(14))!!.name)
    }
}
