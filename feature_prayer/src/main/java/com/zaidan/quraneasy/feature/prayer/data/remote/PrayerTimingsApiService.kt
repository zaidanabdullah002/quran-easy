package com.zaidan.quraneasy.feature.prayer.data.remote

import com.zaidan.quraneasy.feature.prayer.data.remote.dto.PrayerTimingsResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface PrayerTimingsApiService {
    @GET("timings/{date}")
    suspend fun getPrayerTimings(
        @Path("date") date: String,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("method") method: Int = 2
    ): PrayerTimingsResponse
}
