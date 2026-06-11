package com.zaidan.quraneasy.feature.feeling.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaidan.quraneasy.feature.feeling.data.FeelingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FeelingDetailUiState(
    val isLoading: Boolean = true,
    val feeling: Feeling? = null,
    val verses: List<FeelingVerseUiModel> = emptyList(),
    val message: String? = null
)

@HiltViewModel
class FeelingViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val feelingRepository: FeelingRepository
) : ViewModel() {

    private val feelingId: String = savedStateHandle.get<String>("feelingId") ?: "hope"
    private val _uiState = MutableStateFlow(FeelingDetailUiState())
    val uiState: StateFlow<FeelingDetailUiState> = _uiState.asStateFlow()
    val homeFeelings: StateFlow<List<Feeling>> = kotlinx.coroutines.flow.flow {
        emit(feelingRepository.getHomeFeelings())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        loadFeeling()
    }

    private fun loadFeeling() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, message = null) }
            runCatching {
                feelingRepository.getFeelingDetail(feelingId)
            }.onSuccess { state ->
                _uiState.value = state
            }.onFailure { throwable ->
                _uiState.value = FeelingDetailUiState(
                    isLoading = false,
                    message = throwable.message ?: "Unable to load feeling"
                )
            }
        }
    }
}
