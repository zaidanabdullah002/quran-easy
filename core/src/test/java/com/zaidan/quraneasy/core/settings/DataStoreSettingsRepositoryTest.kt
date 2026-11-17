package com.zaidan.quraneasy.core.settings

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import java.io.File
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class DataStoreSettingsRepositoryTest {
    @get:Rule val temporary = TemporaryFolder()

    @Test fun changesPersistAndReadingResetPreservesTheme() = runBlocking {
        val file = File(temporary.root, "settings.preferences_pb")
        suspend fun withStore(block: suspend (SettingsRepository) -> Unit) {
            val job = SupervisorJob()
            val scope = CoroutineScope(Dispatchers.IO + job)
            val store = PreferenceDataStoreFactory.create(scope = scope, produceFile = { file })
            try { block(DataStoreSettingsRepository(store)) }
            finally { job.cancelAndJoin() }
        }
        withStore { repository ->
            assertEquals(AppSettings(), repository.settings.first())
            repository.setThemeMode(ThemeMode.DARK)
            repository.setQuranTextScale(1.4f)
            repository.setShowTranslatedSurahNames(false)
            repository.setKeepScreenAwakeWhileReading(true)
        }
        withStore { repository ->
            assertEquals(AppSettings(themeMode = ThemeMode.DARK, quranTextScale = 1.4f,
                showTranslatedSurahNames = false, keepScreenAwakeWhileReading = true),
                repository.settings.first())
            repository.resetReadingSettings()
            assertEquals(AppSettings(themeMode = ThemeMode.DARK), repository.settings.first())
        }
        withStore { repository ->
            assertEquals(AppSettings(themeMode = ThemeMode.DARK), repository.settings.first())
        }
    }
}
