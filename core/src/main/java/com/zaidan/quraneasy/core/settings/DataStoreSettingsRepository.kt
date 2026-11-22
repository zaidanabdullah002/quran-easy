package com.zaidan.quraneasy.core.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val SETTINGS_FILE_NAME = "app_settings"

val Context.appSettingsDataStore: DataStore<Preferences> by preferencesDataStore(SETTINGS_FILE_NAME)

fun Context.createSettingsRepository(): SettingsRepository =
    DataStoreSettingsRepository(appSettingsDataStore)

class DataStoreSettingsRepository(
    private val dataStore: DataStore<Preferences>,
) : SettingsRepository {
    override val settings: Flow<AppSettings> = dataStore.data
        .map(::decode)

    override suspend fun setThemeMode(mode: ThemeMode) = update {
        it[THEME_MODE] = mode.storageValue
    }

    override suspend fun setQuranFont(font: QuranFontChoice) = update {
        it[QURAN_FONT] = font.storageValue
    }

    override suspend fun setQuranTextScale(scale: Float) = update {
        it[QURAN_TEXT_SCALE] = scale.toValidQuranTextScale()
    }

    override suspend fun setShowTranslatedSurahNames(show: Boolean) = update {
        it[SHOW_TRANSLATED_SURAH_NAMES] = show
    }

    override suspend fun setKeepScreenAwakeWhileReading(keepAwake: Boolean) = update {
        it[KEEP_SCREEN_AWAKE_WHILE_READING] = keepAwake
    }

    override suspend fun resetReadingSettings() = update {
        it[QURAN_FONT] = QuranFontChoice.AL_QALAM.storageValue
        it[QURAN_TEXT_SCALE] = DEFAULT_QURAN_TEXT_SCALE
        it[SHOW_TRANSLATED_SURAH_NAMES] = true
        it[KEEP_SCREEN_AWAKE_WHILE_READING] = false
    }

    override suspend fun setTourStatus(status: TourStatus) = update {
        it[TOUR_STATUS] = status.storageValue
    }

    private suspend fun update(block: (androidx.datastore.preferences.core.MutablePreferences) -> Unit) {
        dataStore.edit(block)
    }

    private fun decode(preferences: Preferences): AppSettings = AppSettings(
        themeMode = ThemeMode.fromStorage(preferences[THEME_MODE]),
        quranFont = QuranFontChoice.fromStorage(preferences[QURAN_FONT]),
        quranTextScale = (preferences[QURAN_TEXT_SCALE] ?: DEFAULT_QURAN_TEXT_SCALE).toValidQuranTextScale(),
        showTranslatedSurahNames = preferences[SHOW_TRANSLATED_SURAH_NAMES] ?: true,
        keepScreenAwakeWhileReading = preferences[KEEP_SCREEN_AWAKE_WHILE_READING] ?: false,
        tourStatus = TourStatus.fromStorage(preferences[TOUR_STATUS]),
    )

    private companion object {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val QURAN_FONT = stringPreferencesKey("quran_font")
        val QURAN_TEXT_SCALE = floatPreferencesKey("quran_text_scale")
        val SHOW_TRANSLATED_SURAH_NAMES = booleanPreferencesKey("show_translated_surah_names")
        val KEEP_SCREEN_AWAKE_WHILE_READING = booleanPreferencesKey("keep_screen_awake_while_reading")
        val TOUR_STATUS = stringPreferencesKey("tour_status")
    }
}
