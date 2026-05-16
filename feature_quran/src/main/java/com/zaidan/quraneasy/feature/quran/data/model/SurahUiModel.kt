package com.zaidan.quraneasy.feature.quran.data.model

data class SurahUiModel(
    val number: Int,
    val englishName: String,
    val translation: String,
    val verses: Int,
    val arabicName: String
)