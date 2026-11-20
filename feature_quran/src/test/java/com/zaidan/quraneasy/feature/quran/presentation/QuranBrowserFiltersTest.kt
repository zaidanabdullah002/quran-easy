package com.zaidan.quraneasy.feature.quran.presentation

import com.zaidan.quraneasy.feature.quran.presentation.model.QuranBookmarkUiModel
import com.zaidan.quraneasy.feature.quran.presentation.model.SurahUiModel
import org.junit.Assert.*
import org.junit.Test

class QuranBrowserFiltersTest {
    private val surah = SurahUiModel(36, "Ya-Sin", "Ya Sin", 83, "يٰسٓ", revelationType = "Meccan")

    @Test fun searchIgnoresSeparatorsCaseAndVocalization() {
        listOf("yasin", " YA SIN ", "Ya-Sin", "يس", "36").forEach {
            assertTrue(it, matchesSurah(surah, it, ChapterFilter.ALL))
        }
        assertFalse(matchesSurah(surah, "999", ChapterFilter.ALL))
    }

    @Test fun filtersIntersectWithSearchAndDoNotClassifyUnknownMetadata() {
        assertTrue(matchesSurah(surah, "36", ChapterFilter.MAKKI))
        assertFalse(matchesSurah(surah, "36", ChapterFilter.MADANI))
        val unknown = surah.copy(revelationType = "")
        assertFalse(ChapterFilter.MAKKI.accepts(unknown))
        assertFalse(ChapterFilter.MADANI.accepts(unknown))
        assertTrue(ChapterFilter.ALL.accepts(unknown))
        assertEquals("", revelationLabel(""))
    }

    @Test fun bookmarkSearchSupportsBothTypesAndTranslations() {
        val chapter = QuranBookmarkUiModel.SurahBookmark(1, 1234, 1, "Al-Fatihah", "الفاتحة", "The Opening")
        val juz = QuranBookmarkUiModel.JuzBookmark(2, 1234, 30)
        assertTrue(matchesBookmark(chapter, "opening"))
        assertTrue(matchesBookmark(chapter, "الفاتحة"))
        assertTrue(matchesBookmark(juz, "Juz 30"))
        assertTrue(matchesBookmark(juz, "30"))
        assertFalse(matchesBookmark(juz, "31"))
        assertTrue(matchesBookmark(juz, " "))
    }
}
