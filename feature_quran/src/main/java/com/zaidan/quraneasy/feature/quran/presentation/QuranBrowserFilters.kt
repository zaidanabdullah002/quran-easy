package com.zaidan.quraneasy.feature.quran.presentation

import com.zaidan.quraneasy.feature.quran.presentation.model.QuranBookmarkUiModel
import com.zaidan.quraneasy.feature.quran.presentation.model.SurahUiModel

internal enum class ChapterFilter(val label: String) {
    ALL("All Chapters"), MAKKI("Makki"), MADANI("Madani");

    fun accepts(surah: SurahUiModel): Boolean = when (this) {
        ALL -> true
        MAKKI -> surah.revelationType.equals("Meccan", true)
        MADANI -> surah.revelationType.equals("Medinan", true)
    }
}

internal fun revelationLabel(type: String): String = when {
    type.equals("Meccan", true) -> "Makki"
    type.equals("Medinan", true) -> "Madani"
    else -> ""
}

// Ignore separators and Arabic vocalization when matching names; preserve digits.
private fun String.searchKey() = lowercase().replace(Regex("[\\p{M}\\s'’\\-ـ]"), "")

internal fun matchesSurah(surah: SurahUiModel, query: String, filter: ChapterFilter): Boolean {
    if (!filter.accepts(surah)) return false
    val key = query.trim().searchKey()
    return key.isEmpty() || surah.number.toString().startsWith(key) ||
        listOf(displaySurahName(surah.number, surah.englishName), surah.englishName, surah.arabicName, surah.translation)
            .any { it.searchKey().contains(key) }
}

/** Stable reader-facing spellings. The upstream API uses legacy doubled-vowel
 * transliterations (for example Al-Faatiha), which do not match the product
 * design or common English search terms. */
private val canonicalSurahNames = listOf(
    "Al-Fatihah", "Al-Baqarah", "Ali 'Imran", "An-Nisa", "Al-Ma'idah", "Al-An'am",
    "Al-A'raf", "Al-Anfal", "At-Tawbah", "Yunus", "Hud", "Yusuf", "Ar-Ra'd", "Ibrahim",
    "Al-Hijr", "An-Nahl", "Al-Isra", "Al-Kahf", "Maryam", "Ta-Ha", "Al-Anbiya", "Al-Hajj",
    "Al-Mu'minun", "An-Nur", "Al-Furqan", "Ash-Shu'ara", "An-Naml", "Al-Qasas",
    "Al-'Ankabut", "Ar-Rum", "Luqman", "As-Sajdah", "Al-Ahzab", "Saba", "Fatir", "Ya-Sin",
    "As-Saffat", "Sad", "Az-Zumar", "Ghafir", "Fussilat", "Ash-Shura", "Az-Zukhruf",
    "Ad-Dukhan", "Al-Jathiyah", "Al-Ahqaf", "Muhammad", "Al-Fath", "Al-Hujurat", "Qaf",
    "Adh-Dhariyat", "At-Tur", "An-Najm", "Al-Qamar", "Ar-Rahman", "Al-Waqi'ah", "Al-Hadid",
    "Al-Mujadilah", "Al-Hashr", "Al-Mumtahanah", "As-Saff", "Al-Jumu'ah", "Al-Munafiqun",
    "At-Taghabun", "At-Talaq", "At-Tahrim", "Al-Mulk", "Al-Qalam", "Al-Haqqah", "Al-Ma'arij",
    "Nuh", "Al-Jinn", "Al-Muzzammil", "Al-Muddaththir", "Al-Qiyamah", "Al-Insan",
    "Al-Mursalat", "An-Naba", "An-Nazi'at", "'Abasa", "At-Takwir", "Al-Infitar",
    "Al-Mutaffifin", "Al-Inshiqaq", "Al-Buruj", "At-Tariq", "Al-A'la", "Al-Ghashiyah",
    "Al-Fajr", "Al-Balad", "Ash-Shams", "Al-Layl", "Ad-Duha", "Ash-Sharh", "At-Tin",
    "Al-'Alaq", "Al-Qadr", "Al-Bayyinah", "Az-Zalzalah", "Al-'Adiyat", "Al-Qari'ah",
    "At-Takathur", "Al-'Asr", "Al-Humazah", "Al-Fil", "Quraysh", "Al-Ma'un", "Al-Kawthar",
    "Al-Kafirun", "An-Nasr", "Al-Masad", "Al-Ikhlas", "Al-Falaq", "An-Nas"
)

internal fun displaySurahName(number: Int, fallback: String): String =
    canonicalSurahNames.getOrNull(number - 1) ?: fallback

internal fun displayArabicSurahName(raw: String): String = raw
    .replace(Regex("[\\u064B-\\u065F\\u0670\\u06D6-\\u06ED]"), "")
    .replace('ٱ', 'ا')
    .replace(Regex("^\\s*سورة\\s*"), "")
    .trim()

internal fun matchesBookmark(bookmark: QuranBookmarkUiModel, query: String): Boolean {
    val key = query.trim().searchKey()
    if (key.isEmpty()) return true
    return when (bookmark) {
        is QuranBookmarkUiModel.SurahBookmark -> bookmark.surahNumber.toString().startsWith(key) ||
            listOf(bookmark.englishName, bookmark.arabicName, bookmark.translation).any { it.searchKey().contains(key) }
        is QuranBookmarkUiModel.JuzBookmark -> bookmark.juzNumber.toString().startsWith(key) ||
            "Juz ${bookmark.juzNumber}".searchKey().contains(key)
    }
}
