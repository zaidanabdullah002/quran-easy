package com.zaidan.quraneasy.feature.prayer.domain.usercase

import com.zaidan.quraneasy.feature.prayer.domain.repository.PrayerRepository
import javax.inject.Inject

class TogglePrayerStatusUseCase @Inject constructor(
    private val repository: PrayerRepository
) {
    suspend operator fun invoke(prayerName: String) {
        repository.updatePrayerCompletion(prayerName)
    }
}