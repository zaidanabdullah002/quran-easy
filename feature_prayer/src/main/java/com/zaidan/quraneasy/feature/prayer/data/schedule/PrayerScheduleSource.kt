package com.zaidan.quraneasy.feature.prayer.data.schedule

import com.zaidan.quraneasy.feature.prayer.domain.PrayerContext
import com.zaidan.quraneasy.feature.prayer.domain.PrayerSchedule

interface PrayerScheduleSource {
    suspend fun getSchedule(context: PrayerContext): PrayerSchedule
}
