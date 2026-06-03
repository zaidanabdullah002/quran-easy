package com.zaidan.quraneasy.feature.quran.presentation.model

data class AyahUiModel(
    val ayahNumber: Int,
    val arabicText: String,
    val translation: String,
    val surahNameEnglish: String,
    val surahNameArabic: String,
    val versesLabel: String
)