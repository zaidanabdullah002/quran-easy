package com.zaidan.quraneasy.feature.prayer.data.remote

import android.util.Log
import com.zaidan.quraneasy.feature.prayer.data.local.entity.PrayerScheduleEntity
import javax.inject.Inject

class PrayerTimingsRemoteDataSource @Inject constructor(
    private val prayerTimingsApiService: PrayerTimingsApiService
) {
    companion object{
        const val TAG = "PrayerTimingsRemoteDataSource"
    }
    suspend fun fetchPrayerTimings(
        date: String,
        latitude: Double,
        longitude: Double,
        locationKey: String,
        locationLabel: String
    ): List<PrayerScheduleEntity> {
        Log.i(TAG,"fetchPrayerTimings()")
        val response = prayerTimingsApiService.getPrayerTimings(date, latitude, longitude)
        Log.i(TAG,"fetchPrayerTimings() response: $response")
        return response.toPrayerScheduleEntities(date, locationKey, locationLabel)
    }
}
