package com.zaidan.quraneasy.feature.prayer.domain.usercase

import com.zaidan.quraneasy.feature.prayer.domain.repository.PrayerRepository
import com.zaidan.quraneasy.feature.prayer.presentation.PrayerUiState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservePrayerStateUseCase @Inject constructor(
    private val repository: PrayerRepository
) {
    operator fun invoke(): Flow<PrayerUiState> = repository.observePrayerStatus()
}