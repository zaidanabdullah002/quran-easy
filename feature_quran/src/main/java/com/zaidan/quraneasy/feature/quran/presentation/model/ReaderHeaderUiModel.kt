package com.zaidan.quraneasy.feature.quran.presentation.model

data class ReaderHeaderUiModel(
    val title: String,
    val subtitle: String,
    val arabicName: String,
    val translation: String = "",
    val revelationType: String = "",
    val juzNumber: Int = 0,
    val revelationOrder: Int = 0,
)
