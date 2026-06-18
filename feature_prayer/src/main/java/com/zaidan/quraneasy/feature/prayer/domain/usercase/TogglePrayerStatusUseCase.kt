package com.zaidan.quraneasy.feature.prayer.domain.usercase

import com.zaidan.quraneasy.feature.prayer.domain.repository.PrayerRepository
import javax.inject.Inject

class TogglePrayerStatusUseCase @Inject constructor(
    private val repository: PrayerRepository
) {
    suspend operator fun invoke(date: String, prayerName: String) {
        repository.togglePrayerCompletion(date, prayerName)
    }
}
