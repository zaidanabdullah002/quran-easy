package com.zaidan.quraneasy.feature.prayer.domain.repository

import com.zaidan.quraneasy.feature.prayer.domain.PrayerDataResult
import com.zaidan.quraneasy.feature.prayer.domain.PrayerState
import kotlinx.coroutines.flow.Flow

interface PrayerRepository {
    fun observePrayerState(date: String, locationKey: String): Flow<PrayerState>
    suspend fun getCachedLocationKey(date: String): String?
    suspend fun getCachedLocationLabel(date: String, locationKey: String): String?
    suspend fun refreshPrayerSchedule(
        date: String,
        latitude: Double,
        longitude: Double,
        locationKey: String,
        locationLabel: String
    ): PrayerDataResult
    suspend fun togglePrayerCompletion(date: String, prayerName: String)
}
