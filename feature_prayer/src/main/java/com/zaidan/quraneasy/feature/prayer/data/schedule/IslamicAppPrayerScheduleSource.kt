package com.zaidan.quraneasy.feature.prayer.data.schedule

import com.zaidan.quraneasy.feature.prayer.data.remote.IslamicAppApiService
import com.zaidan.quraneasy.feature.prayer.domain.PrayerContext
import com.zaidan.quraneasy.feature.prayer.domain.PrayerSchedule
import com.zaidan.quraneasy.feature.prayer.domain.PrayerScheduleValidator
import com.zaidan.quraneasy.feature.prayer.domain.PrayerSettingsProvider
import com.zaidan.quraneasy.feature.prayer.domain.PrayerTime
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IslamicAppPrayerScheduleSource @Inject constructor(
    private val api: IslamicAppApiService,
    private val settingsProvider: PrayerSettingsProvider
) : PrayerScheduleSource {
    override suspend fun getSchedule(context: PrayerContext): PrayerSchedule {
        val settings = settingsProvider.settings
        val response = api.getPrayerTimings(
            date = context.date,
            latitude = context.location.latitude,
            longitude = context.location.longitude,
            timezone = TimeZone.getDefault().id,
            method = settings.method.apiId,
            school = settings.school.apiId
        )
        require(response.code in 200..299) { "islamic.app returned ${response.code}" }
        val timings = response.data.timings
        return PrayerScheduleValidator.requireValid(
            PrayerSchedule(
                date = context.date,
                locationKey = context.location.key,
                source = "islamic.app",
                prayers = listOf(
                    PrayerTime("Fajr", timings.fajr.normalizedTime()),
                    PrayerTime("Sunrise", timings.sunrise.normalizedTime()),
                    PrayerTime("Dhuhr", timings.dhuhr.normalizedTime()),
                    PrayerTime("Asr", timings.asr.normalizedTime()),
                    PrayerTime("Maghrib", timings.maghrib.normalizedTime()),
                    PrayerTime("Isha", timings.isha.normalizedTime())
                )
            )
        )
    }
}

internal fun String.normalizedTime(): String =
    Regex("""\b(?:[01]\d|2[0-3]):[0-5]\d\b""").find(this)?.value
        ?: error("Invalid prayer time: $this")
