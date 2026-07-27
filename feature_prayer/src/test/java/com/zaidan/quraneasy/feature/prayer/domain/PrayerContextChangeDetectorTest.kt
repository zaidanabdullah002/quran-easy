package com.zaidan.quraneasy.feature.prayer.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class PrayerContextChangeDetectorTest {
    private val delhi = PrayerLocation(28.6139, 77.2090)
    private val nearbyDelhi = PrayerLocation(28.6200, 77.2150)
    private val gurugram = PrayerLocation(28.4595, 77.0266)

    @Test
    fun `detects all four update cases`() {
        val original = PrayerContext("27-07-2026", delhi)

        assertEquals(
            PrayerContextChange.NONE,
            PrayerContextChangeDetector.detect(original, PrayerContext("27-07-2026", nearbyDelhi))
        )
        assertEquals(
            PrayerContextChange.DATE,
            PrayerContextChangeDetector.detect(original, PrayerContext("28-07-2026", nearbyDelhi))
        )
        assertEquals(
            PrayerContextChange.LOCATION,
            PrayerContextChangeDetector.detect(original, PrayerContext("27-07-2026", gurugram))
        )
        assertEquals(
            PrayerContextChange.DATE_AND_LOCATION,
            PrayerContextChangeDetector.detect(original, PrayerContext("28-07-2026", gurugram))
        )
    }

    @Test
    fun `first context is initial`() {
        assertEquals(
            PrayerContextChange.INITIAL,
            PrayerContextChangeDetector.detect(null, PrayerContext("27-07-2026", delhi))
        )
    }
}
