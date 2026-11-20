package com.zaidan.quraneasy.core.settings

enum class ThemeMode(val storageValue: String) {
    SYSTEM("system"),
    LIGHT("light"),
    DARK("dark");

    companion object {
        fun fromStorage(value: String?): ThemeMode =
            entries.firstOrNull { it.storageValue == value } ?: SYSTEM
    }
}

enum class QuranFontChoice(val storageValue: String) {
    AMIRI("amiri"),
    AL_QALAM("al_qalam");

    companion object {
        fun fromStorage(value: String?): QuranFontChoice =
            entries.firstOrNull { it.storageValue == value } ?: AMIRI
    }
}

data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val quranFont: QuranFontChoice = QuranFontChoice.AMIRI,
    val quranTextScale: Float = DEFAULT_QURAN_TEXT_SCALE,
    val showTranslatedSurahNames: Boolean = true,
    val keepScreenAwakeWhileReading: Boolean = false,
)

const val MIN_QURAN_TEXT_SCALE = 0.8f
const val DEFAULT_QURAN_TEXT_SCALE = 1.0f
const val MAX_QURAN_TEXT_SCALE = 1.4f

fun Float.toValidQuranTextScale(): Float =
    if (isFinite()) coerceIn(MIN_QURAN_TEXT_SCALE, MAX_QURAN_TEXT_SCALE) else DEFAULT_QURAN_TEXT_SCALE
