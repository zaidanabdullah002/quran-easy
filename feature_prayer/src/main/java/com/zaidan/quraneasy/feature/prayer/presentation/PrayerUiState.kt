package com.zaidan.quraneasy.feature.prayer.presentation

import com.zaidan.quraneasy.feature.prayer.domain.Prayer

data class PrayerUiState(
    val prayers : List<Prayer> = emptyList()
)