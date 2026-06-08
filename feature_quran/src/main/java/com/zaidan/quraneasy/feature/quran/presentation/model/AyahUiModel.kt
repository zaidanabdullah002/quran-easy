package com.zaidan.quraneasy.feature.quran.presentation.model

data class AyahUiModel(
    val surahNumber: Int,
    val numberInSurah: Int,
    val arabicText: String,
    val translation: String? = null,
)