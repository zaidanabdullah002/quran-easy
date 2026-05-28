package com.zaidan.quraneasy.feature.prayer.domain.usercase

import com.zaidan.quraneasy.feature.prayer.domain.repository.PrayerRepository
import javax.inject.Inject

class InitPrayerDataUseCase @Inject constructor(
    private val repository: PrayerRepository
) {
    suspend operator fun invoke() {
        repository.initPrayerData()
    }
}
