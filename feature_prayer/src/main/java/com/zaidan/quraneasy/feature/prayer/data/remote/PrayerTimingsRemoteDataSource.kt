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
        Log.i(
            TAG,
            "fetchPrayerTimings(): date=$date lat=$latitude long=$longitude locationKey=$locationKey locationLabel=$locationLabel"
        )
        val response = prayerTimingsApiService.getPrayerTimings(date, latitude, longitude)
        val timings = response.data.timings
        Log.i(
            TAG,
            "fetchPrayerTimings(): code=${response.code} status=${response.status} timezone=${response.data.meta.timezone} fajr=${timings.fajr} dhuhr=${timings.dhuhr} maghrib=${timings.maghrib}"
        )
        return response.toPrayerScheduleEntities(date, locationKey, locationLabel).also { schedules ->
            Log.d(TAG, "fetchPrayerTimings(): mappedSchedules=${schedules.size} first=${schedules.firstOrNull()}")
        }
    }
}
