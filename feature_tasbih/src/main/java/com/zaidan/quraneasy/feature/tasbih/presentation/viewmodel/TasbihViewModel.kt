package com.zaidan.quraneasy.feature.tasbih.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaidan.quraneasy.feature.tasbih.domain.TasbihState
import com.zaidan.quraneasy.feature.tasbih.domain.usecase.ObserveTasbihStateUseCase
import com.zaidan.quraneasy.feature.tasbih.domain.usecase.UpdateTasbihCountUseCase
import com.zaidan.quraneasy.feature.tasbih.domain.usecase.UpdateTasbihTargetUseCase
import com.zaidan.quraneasy.feature.tasbih.presentation.TasbihUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class TasbihViewModel @Inject constructor(
    observeTasbihStateUseCase: ObserveTasbihStateUseCase,
    private val updateTasbihCountUseCase: UpdateTasbihCountUseCase,
    private val updateTasbihTargetUseCase: UpdateTasbihTargetUseCase
) : ViewModel() {

    val uiStateFlow: StateFlow<TasbihUiState> = observeTasbihStateUseCase()
        .map { state -> state.toUiState() }
        .stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
            initialValue = TasbihUiState()
        )

    fun incrementCount() {
        val newCount = uiStateFlow.value.count + 1
        viewModelScope.launch {
            updateTasbihCountUseCase(newCount)
        }
    }

    fun resetCount() {
        viewModelScope.launch {
            updateTasbihCountUseCase(0)
        }
    }

    fun setTarget(target: Int) {
        viewModelScope.launch {
            updateTasbihTargetUseCase(target)
        }
    }

    private fun TasbihState.toUiState(): TasbihUiState {
        return TasbihUiState(
            count = count,
            target = target,
            tasbihText = tasbihText
        )
    }
}
