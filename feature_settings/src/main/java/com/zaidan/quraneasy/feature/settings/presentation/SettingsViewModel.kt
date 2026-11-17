package com.zaidan.quraneasy.feature.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaidan.quraneasy.core.settings.*
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val settings: AppSettings? = null,
    val loading: Boolean = true,
    val saving: Boolean = false,
    val readError: Boolean = false,
    val saveError: Boolean = false,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(private val repository: SettingsRepository) : ViewModel() {
    private val mutableState = MutableStateFlow(SettingsUiState())
    val state = mutableState.asStateFlow()
    private var observer: Job? = null

    init { reload() }

    fun reload() {
        observer?.cancel()
        mutableState.update { it.copy(loading = true, readError = false) }
        observer = viewModelScope.launch {
            try {
                repository.settings.collect { value ->
                    mutableState.update { it.copy(settings = value, loading = false, readError = false) }
                }
            } catch (_: IOException) {
                mutableState.update { it.copy(loading = false, readError = true) }
            }
        }
    }

    fun theme(value: ThemeMode) = save { repository.setThemeMode(value) }
    fun scale(value: Float) = save { repository.setQuranTextScale(value) }
    fun translatedNames(value: Boolean) = save { repository.setShowTranslatedSurahNames(value) }
    fun keepAwake(value: Boolean) = save { repository.setKeepScreenAwakeWhileReading(value) }
    fun reset() = save { repository.resetReadingSettings() }
    fun dismissError() { mutableState.update { it.copy(saveError = false) } }

    private fun save(operation: suspend () -> Unit) {
        if (state.value.saving || state.value.loading || state.value.readError) return
        mutableState.update { it.copy(saving = true, saveError = false) }
        viewModelScope.launch {
            try {
                operation()
                val confirmed = repository.settings.first()
                mutableState.update { it.copy(settings = confirmed, saving = false) }
            } catch (_: IOException) {
                mutableState.update { it.copy(saving = false, saveError = true) }
            }
        }
    }
}

