package com.zaidan.quraneasy.feature.prayer.data.remote.dto

import com.squareup.moshi.Json

data class PrayerTimingsResponse(
    val code: Int,
    val status: String,
    val data: PrayerTimingsData
)

data class PrayerTimingsData(
    val timings: TimingsDto,
    val date: DateDto,
    val meta: MetaDto
)

data class TimingsDto(
    @Json(name = "Fajr") val fajr: String,
    @Json(name = "Sunrise") val sunrise: String,
    @Json(name = "Dhuhr") val dhuhr: String,
    @Json(name = "Asr") val asr: String,
    @Json(name = "Sunset") val sunset: String,
    @Json(name = "Maghrib") val maghrib: String,
    @Json(name = "Isha") val isha: String,
    @Json(name = "Imsak") val imsak: String,
    @Json(name = "Midnight") val midnight: String,
    @Json(name = "Firstthird") val firstThird: String,
    @Json(name = "Lastthird") val lastThird: String
)

data class DateDto(
    val readable: String,
    val timestamp: String,
    val hijri: HijriDto,
    val gregorian: GregorianDto
)

data class HijriDto(
    val date: String,
    val format: String,
    val day: String,
    val weekday: LocalizedWeekdayDto,
    val month: HijriMonthDto,
    val year: String,
    val designation: DesignationDto,
    val holidays: List<String>,
    val adjustedHolidays: List<String>,
    val method: String
)

data class GregorianDto(
    val date: String,
    val format: String,
    val day: String,
    val weekday: GregorianWeekdayDto,
    val month: GregorianMonthDto,
    val year: String,
    val designation: DesignationDto,
    val lunarSighting: Boolean
)

data class LocalizedWeekdayDto(
    val en: String,
    val ar: String
)

data class GregorianWeekdayDto(
    val en: String
)

data class HijriMonthDto(
    val number: Int,
    val en: String,
    val ar: String,
    val days: Int
)

data class GregorianMonthDto(
    val number: Int,
    val en: String
)

data class DesignationDto(
    val abbreviated: String,
    val expanded: String
)

data class MetaDto(
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val method: MethodDto,
    val latitudeAdjustmentMethod: String,
    val midnightMode: String,
    val school: String,
    val offset: OffsetDto
)

data class MethodDto(
    val id: Int,
    val name: String,
    val params: MethodParamsDto,
    val location: CoordinatesDto
)

data class MethodParamsDto(
    @Json(name = "Fajr") val fajr: Int,
    @Json(name = "Isha") val isha: Int
)

data class CoordinatesDto(
    val latitude: Double,
    val longitude: Double
)

data class OffsetDto(
    @Json(name = "Imsak") val imsak: Int,
    @Json(name = "Fajr") val fajr: Int,
    @Json(name = "Sunrise") val sunrise: Int,
    @Json(name = "Dhuhr") val dhuhr: Int,
    @Json(name = "Asr") val asr: Int,
    @Json(name = "Maghrib") val maghrib: Int,
    @Json(name = "Sunset") val sunset: Int,
    @Json(name = "Isha") val isha: Int,
    @Json(name = "Midnight") val midnight: Int
)
