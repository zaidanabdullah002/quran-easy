package com.zaidan.quraneasy.feature.quran.presentation.model

data class AyahUiState(
    val isLoading: Boolean = false,
    val isReady: Boolean = false,
    val message: String? = null,
    val ayahs: List<AyahUiModel> = emptyList()
)
