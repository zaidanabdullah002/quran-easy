package com.zaidan.quraneasy.core.settings

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val settings: Flow<AppSettings>

    suspend fun setThemeMode(mode: ThemeMode)
    suspend fun setQuranFont(font: QuranFontChoice)
    suspend fun setQuranTextScale(scale: Float)
    suspend fun setShowTranslatedSurahNames(show: Boolean)
    suspend fun setKeepScreenAwakeWhileReading(keepAwake: Boolean)
    suspend fun resetReadingSettings()
    suspend fun setTourStatus(status: TourStatus)
}
