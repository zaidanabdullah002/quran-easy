package com.zaidan.quraneasy.feature.feeling.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaidan.quraneasy.feature.feeling.data.FeelingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

@HiltViewModel
class HomeFeelingViewModel @Inject constructor(
    private val feelingRepository: FeelingRepository
) : ViewModel() {
    private val _spotlights = MutableStateFlow<Map<String, FeelingVerseUiModel>>(emptyMap())
    val spotlights: StateFlow<Map<String, FeelingVerseUiModel>> = _spotlights
    private val loading = mutableSetOf<String>()

    fun loadSpotlight(feelingId: String) {
        if (_spotlights.value.containsKey(feelingId) || !loading.add(feelingId)) return
        viewModelScope.launch {
            try {
                feelingRepository.getFeelingVerses(feelingId).firstOrNull()?.let { verse ->
                    _spotlights.value = _spotlights.value + (feelingId to verse)
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                // Keep the preview unavailable instead of pairing unrelated Quran text.
            } finally {
                loading.remove(feelingId)
            }
        }
    }
    val feelings: StateFlow<List<Feeling>> = flow {
        emit(feelingRepository.getHomeFeelings())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )
}
