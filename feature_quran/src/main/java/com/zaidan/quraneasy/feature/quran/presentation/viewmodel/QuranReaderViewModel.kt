package com.zaidan.quraneasy.feature.quran.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaidan.quraneasy.feature.quran.domain.usecase.QuranUseCasesData
import com.zaidan.quraneasy.feature.quran.presentation.model.AyahUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuranReaderViewModel @Inject constructor(
    private val quranUseCases: QuranUseCasesData
) : ViewModel() {

    companion object {
        private const val TAG = "QuranReaderViewModel"
    }

    private val _ayahUiState = MutableStateFlow(AyahUiState(isLoading = true))
    val ayahUiState: StateFlow<AyahUiState> = _ayahUiState.asStateFlow()

    fun loadAyahWithSurahNumber(surahNumber: Int) {
        Log.i(TAG, "loadAyahWithSurahNumber: $surahNumber")
        viewModelScope.launch {
            _ayahUiState.update {
                it.copy(isLoading = true, message = "")
            }

            runCatching {
                Log.i(TAG, "downloadingAyahWithSurahNumber: $surahNumber")
                quranUseCases.ensureSurahDownloaded(surahNumber)
            }.onSuccess {
                Log.i(TAG, "downloadingAyahWithSurahNumber: success $surahNumber")
                val ayahs = quranUseCases.observeSurah(surahNumber).first()
                Log.i(TAG, "loadAyahWithSurahNumber ayahList: $ayahs")

                _ayahUiState.update {
                    it.copy(
                        isLoading = false,
                        isReady = true,
                        message = "",
                        ayahs = ayahs
                    )
                }
            }.onFailure { throwable ->
                Log.i(TAG, "downloadingAyahWithSurahNumber: failed $surahNumber, ${throwable.message}")
                _ayahUiState.update {
                    it.copy(
                        isLoading = false,
                        isReady = false,
                        message = throwable.message ?: "surahNumber: $surahNumber Download Failed"
                    )
                }
            }
        }
    }

    fun loadAyahWithJuzNumber(juzNumber: Int) {
        Log.i(TAG, "loadAyahWithJuzNumber: $juzNumber")
        viewModelScope.launch {
            _ayahUiState.update {
                it.copy(isLoading = true, message = "")
            }

            runCatching {
                Log.i(TAG, "downloadingAyahWithJuzNumber: $juzNumber")
                quranUseCases.ensureJuzDownloaded(juzNumber)
            }.onSuccess {
                Log.i(TAG, "downloadingAyahWithJuzNumber: success $juzNumber")
                val ayahs = quranUseCases.observeJuz(juzNumber).first()
                Log.i(TAG, "loadAyahWithJuzNumber ayahList: $ayahs")

                _ayahUiState.update {
                    it.copy(
                        isLoading = false,
                        isReady = true,
                        message = "",
                        ayahs = ayahs
                    )
                }
            }.onFailure { throwable ->
                Log.i(TAG, "downloadingAyahWithJuzNumber: failed $juzNumber, ${throwable.message}")
                _ayahUiState.update {
                    it.copy(
                        isLoading = false,
                        isReady = false,
                        message = throwable.message ?: "juzNumber: $juzNumber Download Failed"
                    )
                }
            }
        }
    }
}
