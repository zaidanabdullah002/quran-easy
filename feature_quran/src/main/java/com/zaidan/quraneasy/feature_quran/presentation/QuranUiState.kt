package com.zaidan.quraneasy.feature_quran.presentation

import com.zaidan.quraneasy.feature_quran.model.Surah

data class QuranUiState(
    val surahs: List<Surah> = emptyList(),
    val isLoading: Boolean = true
)

