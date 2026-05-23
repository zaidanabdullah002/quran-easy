package com.zaidan.quraneasy.feature.prayer.presentation

data class PrayerUiState(
    val name: String,
    val time: String,
    val completed: Boolean = false
)