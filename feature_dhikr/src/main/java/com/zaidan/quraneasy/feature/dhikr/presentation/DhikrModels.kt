package com.zaidan.quraneasy.feature.dhikr.presentation

data class DhikrCategory(
    val title: String,
    val subtitle: String,
    val selected: Boolean = false
)

data class DhikrItem(
    val countLabel: String,
    val reference: String,
    val arabic: String,
    val transliteration: String,
    val translation: String,
    val completed: Boolean = false
)
