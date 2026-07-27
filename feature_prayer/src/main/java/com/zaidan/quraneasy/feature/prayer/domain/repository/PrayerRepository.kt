package com.zaidan.quraneasy.feature.prayer.domain.repository

import com.zaidan.quraneasy.feature.prayer.domain.PrayerState
import com.zaidan.quraneasy.feature.prayer.domain.PrayerSchedule
import kotlinx.coroutines.flow.Flow

interface PrayerRepository {
    fun observePrayerState(date: String, locationKey: String): Flow<PrayerState>
    suspend fun getCachedLocationKey(date: String): String?
    suspend fun getCachedLocationLabel(date: String, locationKey: String): String?
    suspend fun hasSchedule(date: String, locationKey: String): Boolean
    suspend fun saveSchedule(schedule: PrayerSchedule, locationLabel: String)
    suspend fun updateLocationLabel(date: String, locationKey: String, locationLabel: String)
    suspend fun togglePrayerCompletion(date: String, prayerName: String)
}
