package com.zaidan.quraneasy.feature.prayer.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaidan.quraneasy.feature.prayer.domain.PrayerState
import com.zaidan.quraneasy.feature.prayer.domain.usercase.InitPrayerDataUseCase
import com.zaidan.quraneasy.feature.prayer.domain.usercase.ObservePrayerStateUseCase
import com.zaidan.quraneasy.feature.prayer.domain.usercase.TogglePrayerStatusUseCase
import com.zaidan.quraneasy.feature.prayer.presentation.PrayerUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrayerViewModel @Inject constructor(
    private val observePrayerStateUseCase: ObservePrayerStateUseCase,
    private val togglePrayerStatusUseCase: TogglePrayerStatusUseCase,
    private val initPrayerDataUseCase: InitPrayerDataUseCase
)  : ViewModel(){

    val uiStateFlow: StateFlow<PrayerUiState> = observePrayerStateUseCase()
        .map { state -> state.toUiState() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PrayerUiState()
        )

    init {
        viewModelScope.launch {
            if (observePrayerStateUseCase().first().prayers.isEmpty()) {
                initPrayerDataUseCase()
            }
        }
    }



    fun togglePrayer(index: Int) {
        viewModelScope.launch {
            val prayer = uiStateFlow.value.prayers.getOrNull(index)
            prayer?.let{
                togglePrayerStatusUseCase(it.name)
            }
        }
    }

    private fun PrayerState.toUiState(): PrayerUiState {
        return PrayerUiState(prayers = prayers)
    }
}
