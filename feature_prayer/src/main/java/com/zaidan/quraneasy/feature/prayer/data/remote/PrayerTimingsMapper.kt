package com.zaidan.quraneasy.feature.prayer.data.remote

import com.zaidan.quraneasy.feature.prayer.data.local.entity.PrayerScheduleEntity
import com.zaidan.quraneasy.feature.prayer.data.remote.dto.PrayerTimingsResponse

fun PrayerTimingsResponse.toPrayerScheduleEntities(
    date: String,
    locationKey: String,
    locationLabel: String
): List<PrayerScheduleEntity> {
    val timings = data.timings
    return listOf(
        PrayerScheduleEntity(date, locationKey, locationLabel, "Fajr", timings.fajr),
        PrayerScheduleEntity(date, locationKey, locationLabel, "Dhuhr", timings.dhuhr),
        PrayerScheduleEntity(date, locationKey, locationLabel, "Asr", timings.asr),
        PrayerScheduleEntity(date, locationKey, locationLabel, "Maghrib", timings.maghrib),
        PrayerScheduleEntity(date, locationKey, locationLabel, "Isha", timings.isha)
    )
}
