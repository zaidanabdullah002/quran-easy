package com.zaidan.quraneasy.core.settings

import org.junit.Assert.assertEquals
import org.junit.Test

class AppSettingsTest {
    @Test
    fun `invalid scales fall back or clamp to supported bounds`() {
        assertEquals(DEFAULT_QURAN_TEXT_SCALE, Float.NaN.toValidQuranTextScale(), 0f)
        assertEquals(MIN_QURAN_TEXT_SCALE, 0.2f.toValidQuranTextScale(), 0f)
        assertEquals(MAX_QURAN_TEXT_SCALE, 2f.toValidQuranTextScale(), 0f)
        assertEquals(1.2f, 1.2f.toValidQuranTextScale(), 0f)
    }

    @Test
    fun `unknown persisted enum values use safe defaults`() {
        assertEquals(ThemeMode.SYSTEM, ThemeMode.fromStorage("not-a-theme"))
        assertEquals(QuranFontChoice.AL_QALAM, QuranFontChoice.fromStorage("unknown-font"))
    }
}
