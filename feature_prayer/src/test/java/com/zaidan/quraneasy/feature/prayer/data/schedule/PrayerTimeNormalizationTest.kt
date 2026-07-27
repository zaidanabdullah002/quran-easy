package com.zaidan.quraneasy.feature.prayer.data.schedule

import org.junit.Assert.assertEquals
import org.junit.Test

class PrayerTimeNormalizationTest {
    @Test
    fun `removes provider timezone suffix`() {
        assertEquals("20:43", "20:43 (IST)".normalizedTime())
    }
}
