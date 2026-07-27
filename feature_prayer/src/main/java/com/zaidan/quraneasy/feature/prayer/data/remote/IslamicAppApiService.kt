package com.zaidan.quraneasy.feature.prayer.data.remote

import com.zaidan.quraneasy.feature.prayer.data.remote.dto.IslamicAppTimingsResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface IslamicAppApiService {
    @GET("timings/{date}")
    suspend fun getPrayerTimings(
        @Path("date") date: String,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("timezone") timezone: String,
        @Query("method") method: Int,
        @Query("school") school: Int
    ): IslamicAppTimingsResponse
}
