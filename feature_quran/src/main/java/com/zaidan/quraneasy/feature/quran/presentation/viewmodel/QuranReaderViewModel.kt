package com.zaidan.quraneasy.feature.quran.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaidan.quraneasy.feature.quran.domain.usecase.QuranUseCasesData
import com.zaidan.quraneasy.feature.quran.presentation.ReaderType
import com.zaidan.quraneasy.feature.quran.presentation.audio.QuranAyahAudioPlayer
import com.zaidan.quraneasy.feature.quran.presentation.audio.AyahAudioItem
import com.zaidan.quraneasy.feature.quran.presentation.model.AyahUiState
import com.zaidan.quraneasy.feature.quran.presentation.model.AyahUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import com.zaidan.quraneasy.core.settings.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuranReaderViewModel @Inject constructor(
    private val quranUseCases: QuranUseCasesData,
    private val audioPlayer: QuranAyahAudioPlayer,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    companion object {
        private const val TAG = "QuranReaderViewModel"
    }

    private val _ayahUiState = MutableStateFlow(AyahUiState(isLoading = true))
    val ayahUiState: StateFlow<AyahUiState> = _ayahUiState.asStateFlow()
    val audioUiState = audioPlayer.state
    val surahs = quranUseCases.observeSurahs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    private var bookmarkJob: Job? = null
    private var currentReaderType: Int? = null
    private var currentItemNumber: Int? = null

    fun setAudioPlaylist(ayahs: List<AyahUiModel>) = audioPlayer.setPlaylist(
        ayahs.map { AyahAudioItem(it.surahNumber, it.numberInSurah) }
    )

    fun toggleAudioPlayback() = audioPlayer.togglePlayback()

    fun playAyahAtIndex(index: Int) = audioPlayer.playIndex(index)

    fun playPreviousAyah() = audioPlayer.previous()

    fun playNextAyah() = audioPlayer.next()

    fun stopAudio() = audioPlayer.stop()

    fun cycleTextScale() {
        viewModelScope.launch {
            val currentScale = settingsRepository.settings.first().quranTextScale
            val nextScale = if (currentScale >= 1.29f) 0.9f else (currentScale + 0.1f)
            settingsRepository.setQuranTextScale(nextScale)
        }
    }

    fun loadAyahWithSurahNumber(surahNumber: Int) {
        audioPlayer.setPlaylist(emptyList())
        Log.i(TAG, "loadAyahWithSurahNumber: $surahNumber")
        currentReaderType = ReaderType.SURAH.ordinal
        currentItemNumber = surahNumber
        observeBookmarkState()
        viewModelScope.launch {
            _ayahUiState.update {
                it.copy(isLoading = true, isReady = false, ayahs = emptyList(), message = "")
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
        audioPlayer.setPlaylist(emptyList())
        Log.i(TAG, "loadAyahWithJuzNumber: $juzNumber")
        currentReaderType = ReaderType.JUZ.ordinal
        currentItemNumber = juzNumber
        observeBookmarkState()
        viewModelScope.launch {
            _ayahUiState.update {
                it.copy(isLoading = true, isReady = false, ayahs = emptyList(), message = "")
            }

            runCatching {
                Log.i(TAG, "downloadingAyahWithJuzNumber: $juzNumber")
                quranUseCases.ensureJuzDownloaded(juzNumber)
            }.onSuccess {
                Log.i(TAG, "downloadingAyahWithJuzNumber: success $juzNumber")
                val ayahs = quranUseCases.observeJuz(juzNumber).first()
                Log.i(TAG, "loadAyahWithJuzNumber ayahList: $ayahs")

                if (ayahs.isEmpty()) {
                    _ayahUiState.update {
                        it.copy(
                            isLoading = false,
                            isReady = false,
                            message = "No ayahs found for Juz $juzNumber after download."
                        )
                    }
                } else {
                    _ayahUiState.update {
                        it.copy(
                            isLoading = false,
                            isReady = true,
                            message = "",
                            ayahs = ayahs
                        )
                    }
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

    fun toggleBookmark() {
        val readerType = currentReaderType ?: return
        val itemNumber = currentItemNumber ?: return

        viewModelScope.launch {
            when (readerType) {
                ReaderType.SURAH.ordinal -> {
                    if (_ayahUiState.value.isBookmarked) {
                        quranUseCases.removeSurahBookmark(itemNumber)
                    } else {
                        quranUseCases.addSurahBookmark(itemNumber)
                    }
                }

                ReaderType.JUZ.ordinal -> {
                    if (_ayahUiState.value.isBookmarked) {
                        quranUseCases.removeJuzBookmark(itemNumber)
                    } else {
                        quranUseCases.addJuzBookmark(itemNumber)
                    }
                }
            }
        }
    }

    private fun observeBookmarkState() {
        bookmarkJob?.cancel()
        val readerType = currentReaderType ?: return
        val itemNumber = currentItemNumber ?: return
        bookmarkJob = viewModelScope.launch {
            val flow = when (readerType) {
                ReaderType.SURAH.ordinal -> quranUseCases.isSurahBookmarked(itemNumber)
                ReaderType.JUZ.ordinal -> quranUseCases.isJuzBookmarked(itemNumber)
                else -> return@launch
            }
            flow.collectLatest { bookmarked ->
                _ayahUiState.update {
                    it.copy(isBookmarked = bookmarked)
                }
            }
        }
    }

    override fun onCleared() {
        audioPlayer.release()
        super.onCleared()
    }
}
