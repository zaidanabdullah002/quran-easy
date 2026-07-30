package com.zaidan.quraneasy.feature.feeling.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaidan.quraneasy.feature.feeling.data.FeelingRepository
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
    private val feelingRepository: FeelingRepository
) : ViewModel() {

    private val feelingId: String = savedStateHandle.get<String>("feelingId") ?: "hope"
    private val _uiState = MutableStateFlow(FeelingDetailUiState())
    val uiState: StateFlow<FeelingDetailUiState> = _uiState.asStateFlow()

    init {
        loadFeeling()
    }

    private fun loadFeeling() {
        viewModelScope.launch {
            val feeling = runCatching {
                feelingRepository.getFeeling(feelingId)
            }.getOrElse { throwable ->
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
                _uiState.value = FeelingDetailUiState(
                    isLoading = false,
                    feeling = feeling,
                    message = throwable.message ?: "Unable to load feeling"
                )
            }
        }
    }
}
