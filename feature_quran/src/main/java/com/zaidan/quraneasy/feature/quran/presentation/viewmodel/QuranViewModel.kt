package com.zaidan.quraneasy.feature.quran.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaidan.quraneasy.feature.quran.data.local.QuranPreferences
import com.zaidan.quraneasy.feature.quran.domain.usecase.QuranUseCasesData
import com.zaidan.quraneasy.feature.quran.presentation.model.JuzUiModel
import com.zaidan.quraneasy.feature.quran.presentation.model.QuranUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuranViewModel @Inject constructor(
    private val quranUseCases: QuranUseCasesData,
    private val quranPreferences: QuranPreferences
) : ViewModel() {

    companion object{
        private const val TAG = "QuranViewModel"
    }
    private val _uiState = MutableStateFlow(
        QuranUiState(
            selectedTab = quranPreferences.getSelectedTab(),
            isLoading = true
        )
    )
    private val surahs = quranUseCases.observeSurahs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    private val juzs = MutableStateFlow(
        (1..30).map { juzNumber ->
            JuzUiModel(
                juzNum = juzNumber,
                englishName = "Juz $juzNumber",
                verses = 0
            )
        }
    )
    val uiState: StateFlow<QuranUiState> = combine(
        _uiState,
        surahs,
        juzs
    ) { baseState, surahList, juzList ->
        baseState.copy(
            surahs = surahList,
            juzs = juzList
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        QuranUiState(isLoading = true)
    )

    init {
        Log.i(TAG,"init")
        loadSurahList()
    }

    fun selectTab(tab: Int) {
        Log.i(TAG,"selectTab: $tab")
        quranPreferences.saveSelectedTab(tab)
        _uiState.update {
            it.copy(selectedTab = tab)
        }
    }


    fun loadSurahList() {
        Log.i(TAG,"loadSurahList")
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, message = "") }

            runCatching {
                quranUseCases.ensureSurahListDownloaded()
            }.onSuccess {
                Log.i(TAG,"loadSurahList: success")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isReady = true,
                        message = ""
                    )
                }
            }.onFailure { throwable ->
                Log.i(TAG,"loadSurahList: failed")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isReady = false,
                        message = throwable.message ?: "Download Failed"
                    )
                }
            }
        }
    }
}
