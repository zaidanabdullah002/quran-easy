package com.zaidan.quraneasy.feature.feeling.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaidan.quraneasy.feature.feeling.data.FeelingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeFeelingViewModel @Inject constructor(
    feelingRepository: FeelingRepository
) : ViewModel() {
    val feelings: StateFlow<List<Feeling>> = flow {
        emit(feelingRepository.getHomeFeelings())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )
}
