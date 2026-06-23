package com.zaidan.quraneasy.feature.quran.presentation.model

data class QuranUiState(
    val selectedTab: Int = 0,
    val isLoading: Boolean = false,
    val isReady: Boolean = false,
    val message: String = "",
    val surahs: List<SurahUiModel> = emptyList(),
    val juzs: List<JuzUiModel> = emptyList(),
    val bookmarks: List<QuranBookmarkUiModel> = emptyList()
)
