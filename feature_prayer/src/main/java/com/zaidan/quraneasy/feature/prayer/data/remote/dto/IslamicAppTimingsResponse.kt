package com.zaidan.quraneasy.feature.prayer.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class IslamicAppTimingsResponse(
    val code: Int,
    val status: String,
    val data: IslamicAppData
)

@JsonClass(generateAdapter = true)
data class IslamicAppData(
    val timings: IslamicAppTimings,
    val meta: IslamicAppMeta
)

@JsonClass(generateAdapter = true)
data class IslamicAppTimings(
    @Json(name = "Fajr") val fajr: String,
    @Json(name = "Sunrise") val sunrise: String,
    @Json(name = "Dhuhr") val dhuhr: String,
    @Json(name = "Asr") val asr: String,
    @Json(name = "Maghrib") val maghrib: String,
    @Json(name = "Isha") val isha: String
)

@JsonClass(generateAdapter = true)
data class IslamicAppMeta(
    val timezone: String? = null
)
