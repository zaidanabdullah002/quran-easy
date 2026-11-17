package com.zaidan.quraneasy.feature.settings.presentation

import androidx.lifecycle.viewModelScope
import com.zaidan.quraneasy.core.settings.*
import java.io.IOException
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private lateinit var model: SettingsViewModel

    @Before fun setup() { Dispatchers.setMain(dispatcher) }
    @After fun teardown() {
        if (::model.isInitialized) model.viewModelScope.cancel()
        Dispatchers.resetMain()
    }

    @Test fun readFailureCanBeRetriedWithoutShowingDefaultsAsSaved() = runTest(dispatcher) {
        val repo = FakeRepository().apply { failRead = true }
        model = SettingsViewModel(repo)
        runCurrent()
        assertTrue(model.state.value.readError)
        assertNull(model.state.value.settings)
        repo.failRead = false
        model.reload()
        runCurrent()
        assertFalse(model.state.value.readError)
        assertEquals(repo.values.value, model.state.value.settings)
    }

    @Test fun failedSaveKeepsConfirmedValueAndAllowsAnotherAttempt() = runTest(dispatcher) {
        val repo = FakeRepository()
        model = SettingsViewModel(repo)
        runCurrent()
        repo.failWrite = true
        model.theme(ThemeMode.DARK)
        runCurrent()
        assertTrue(model.state.value.saveError)
        assertFalse(model.state.value.saving)
        assertEquals(ThemeMode.SYSTEM, model.state.value.settings?.themeMode)
        repo.failWrite = false
        model.theme(ThemeMode.DARK)
        runCurrent()
        assertFalse(model.state.value.saveError)
        assertEquals(ThemeMode.DARK, model.state.value.settings?.themeMode)
    }

    @Test fun pendingSliderWriteBlocksRacingReset() = runTest(dispatcher) {
        val repo = FakeRepository()
        model = SettingsViewModel(repo)
        runCurrent()
        repo.gate = CompletableDeferred()
        model.scale(1.4f)
        runCurrent()
        assertTrue(model.state.value.saving)
        model.reset()
        repo.gate!!.complete(Unit)
        runCurrent()
        assertEquals(0, repo.resetCalls)
        assertEquals(1.4f, model.state.value.settings!!.quranTextScale)
        assertFalse(model.state.value.saving)
    }

    private class FakeRepository : SettingsRepository {
        val values = MutableStateFlow(AppSettings())
        var failRead = false
        var failWrite = false
        var gate: CompletableDeferred<Unit>? = null
        var resetCalls = 0
        override val settings: Flow<AppSettings> = flow {
            if (failRead) throw IOException("Read failure")
            emitAll(values)
        }
        private suspend fun write(change: (AppSettings) -> AppSettings) {
            gate?.await()
            if (failWrite) throw IOException("Write failure")
            values.update(change)
        }
        override suspend fun setThemeMode(mode: ThemeMode) = write { it.copy(themeMode = mode) }
        override suspend fun setQuranFont(font: QuranFontChoice) = write { it.copy(quranFont = font) }
        override suspend fun setQuranTextScale(scale: Float) = write { it.copy(quranTextScale = scale) }
        override suspend fun setShowTranslatedSurahNames(show: Boolean) = write { it.copy(showTranslatedSurahNames = show) }
        override suspend fun setKeepScreenAwakeWhileReading(keepAwake: Boolean) = write { it.copy(keepScreenAwakeWhileReading = keepAwake) }
        override suspend fun resetReadingSettings() {
            resetCalls++
            write { AppSettings(themeMode = it.themeMode) }
        }
    }
}
