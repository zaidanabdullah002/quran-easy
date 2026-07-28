package com.zaidan.quraneasy.feature.quran.presentation

import org.junit.Assert.assertEquals
import org.junit.Test

class QuranReaderTextTest {

    @Test
    fun removesBismillahFromDefaultEditionFirstAyah() {
        val firstAyah =
            "بِسۡمِ ٱللَّهِ ٱلرَّحۡمَـٰنِ ٱلرَّحِیمِ الۤمۤ\n"

        assertEquals("الۤمۤ", firstAyah.withoutProviderBismillahPrefix())
    }

    @Test
    fun removesBismillahFromUthmaniEditionFirstAyah() {
        val firstAyah =
            "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ الٓمٓ"

        assertEquals("الٓمٓ", firstAyah.withoutProviderBismillahPrefix())
    }
}
