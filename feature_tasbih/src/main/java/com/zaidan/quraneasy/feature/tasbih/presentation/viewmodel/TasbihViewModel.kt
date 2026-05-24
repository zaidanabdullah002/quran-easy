package com.zaidan.quraneasy.feature.tasbih.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaidan.quraneasy.feature.tasbih.domain.usecase.ObserveTasbihStateUseCase
import com.zaidan.quraneasy.feature.tasbih.domain.usecase.UpdateTasbihCountUseCase
import com.zaidan.quraneasy.feature.tasbih.domain.usecase.UpdateTasbihTargetUseCase
import com.zaidan.quraneasy.feature.tasbih.presentation.TasbihUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class TasbihViewModel @Inject constructor(
    observeTasbihStateUseCase: ObserveTasbihStateUseCase,
    private val updateTasbihCountUseCase: UpdateTasbihCountUseCase,
    private val updateTasbihTargetUseCase: UpdateTasbihTargetUseCase
) : ViewModel() {

    private val _uiStateFlow = MutableStateFlow(TasbihUiState())
    val uiStateFlow: StateFlow<TasbihUiState> = _uiStateFlow.asStateFlow()

    init {
        viewModelScope.launch {
            observeTasbihStateUseCase().collect { state ->
                _uiStateFlow.update {
                    it.copy(
                        count = state.count,
                        target = state.target,
                        tasbihText = state.tasbihText
                    )
                }
            }
        }
    }

    fun incrementCount() {
        val newCount = _uiStateFlow.value.count + 1
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
}
