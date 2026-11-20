package com.zaidan.quraneasy.feature.feeling.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaidan.quraneasy.feature.feeling.data.FeelingRepository
import com.zaidan.quraneasy.feature.quran.presentation.audio.AyahAudioItem
import com.zaidan.quraneasy.feature.quran.presentation.audio.QuranAyahAudioPlayer
import kotlinx.coroutines.Job
import kotlinx.coroutines.CancellationException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FeelingDetailUiState(
    val isLoading: Boolean = true,
    val feeling: Feeling? = null,
    val verses: List<FeelingVerseUiModel> = emptyList(),
    val message: String? = null
)

@HiltViewModel
class FeelingDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val feelingRepository: FeelingRepository,
    private val audioPlayer: QuranAyahAudioPlayer,
) : ViewModel() {

    private val feelingId: String = savedStateHandle.get<String>("feelingId") ?: "hope"
    private val _uiState = MutableStateFlow(FeelingDetailUiState())
    val uiState: StateFlow<FeelingDetailUiState> = _uiState.asStateFlow()

    val audioState = audioPlayer.state
    private var loadJob: Job? = null
    fun stopAudio() = audioPlayer.stop()
    fun listen(index: Int) {
        val queue = _uiState.value.verses.map { AyahAudioItem(it.surahNumber, it.numberInSurah) }
        audioPlayer.setPlaylist(queue)
        if (audioPlayer.state.value.currentIndex == index) audioPlayer.togglePlayback()
        else audioPlayer.playIndex(index)
    }

    init {
        loadFeeling()
    }

    fun loadFeeling() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = FeelingDetailUiState(isLoading = true)
            val feeling = runCatching {
                feelingRepository.getFeeling(feelingId)
            }.getOrElse { throwable ->
                if (throwable is CancellationException) throw throwable
                _uiState.value = FeelingDetailUiState(
                    isLoading = false,
                    message = throwable.message ?: "Unable to load feeling"
                )
                return@launch
            }

            _uiState.value = FeelingDetailUiState(
                isLoading = true,
                feeling = feeling
            )

            runCatching {
                feelingRepository.getFeelingVerses(feelingId)
            }.onSuccess { verses ->
                _uiState.value = FeelingDetailUiState(
                    isLoading = false,
                    feeling = feeling,
                    verses = verses
                )
            }.onFailure { throwable ->
                if (throwable is CancellationException) throw throwable
                _uiState.value = FeelingDetailUiState(
                    isLoading = false,
                    feeling = feeling,
                    message = throwable.message ?: "Unable to load feeling"
                )
            }
        }
    }
}
