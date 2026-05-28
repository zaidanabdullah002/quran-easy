package com.zaidan.quraneasy.feature.prayer.domain.repository

import com.zaidan.quraneasy.feature.prayer.domain.Prayer
import com.zaidan.quraneasy.feature.prayer.domain.PrayerState
import kotlinx.coroutines.flow.Flow

interface PrayerRepository {
    fun observePrayerStatus(): Flow<PrayerState>
    suspend fun updatePrayerCompletion(prayerName: String)
    suspend fun initPrayerData()

}
