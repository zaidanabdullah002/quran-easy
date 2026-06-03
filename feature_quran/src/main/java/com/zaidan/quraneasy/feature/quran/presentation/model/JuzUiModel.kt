package com.zaidan.quraneasy.feature.quran.presentation.model

data class JuzUiModel(
    val juzNum: Int,
    val englishName: String,
    val translation: String,
    val verses: Int,
    val arabicName: String
)