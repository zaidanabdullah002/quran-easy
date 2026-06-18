package com.zaidan.quraneasy.feature.prayer.domain.usercase

import com.zaidan.quraneasy.feature.prayer.domain.PrayerDataResult
import com.zaidan.quraneasy.feature.prayer.domain.repository.PrayerRepository
import javax.inject.Inject

class RefreshPrayerTimingsUseCase @Inject constructor(
    private val repository: PrayerRepository
) {
    suspend operator fun invoke(
        date: String,
        latitude: Double,
        longitude: Double,
        locationKey: String,
        locationLabel: String
    ): PrayerDataResult {
        return repository.refreshPrayerSchedule(date, latitude, longitude, locationKey, locationLabel)
    }
}
