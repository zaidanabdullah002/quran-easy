package com.zaidan.quraneasy.feature.prayer.data.schedule

import com.batoulapps.adhan.CalculationMethod
import com.batoulapps.adhan.Coordinates
import com.batoulapps.adhan.Madhab
import com.batoulapps.adhan.PrayerTimes
import com.batoulapps.adhan.data.DateComponents
import com.zaidan.quraneasy.feature.prayer.domain.PrayerAsrSchool
import com.zaidan.quraneasy.feature.prayer.domain.PrayerCalculationMethod
import com.zaidan.quraneasy.feature.prayer.domain.PrayerContext
import com.zaidan.quraneasy.feature.prayer.domain.PrayerSchedule
import com.zaidan.quraneasy.feature.prayer.domain.PrayerScheduleValidator
import com.zaidan.quraneasy.feature.prayer.domain.PrayerSettingsProvider
import com.zaidan.quraneasy.feature.prayer.domain.PrayerTime
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalPrayerScheduleSource @Inject constructor(
    private val settingsProvider: PrayerSettingsProvider
) : PrayerScheduleSource {
    override suspend fun getSchedule(context: PrayerContext): PrayerSchedule {
        val (day, month, year) = context.date.split("-").map(String::toInt)
        val method = when (settingsProvider.settings.method) {
            PrayerCalculationMethod.KARACHI -> CalculationMethod.KARACHI
            PrayerCalculationMethod.ISNA -> CalculationMethod.NORTH_AMERICA
            PrayerCalculationMethod.MUSLIM_WORLD_LEAGUE -> CalculationMethod.MUSLIM_WORLD_LEAGUE
            PrayerCalculationMethod.UMM_AL_QURA -> CalculationMethod.UMM_AL_QURA
        }
        val parameters = method.parameters.apply {
            madhab = if (settingsProvider.settings.school == PrayerAsrSchool.HANAFI) {
                Madhab.HANAFI
            } else {
                Madhab.SHAFI
            }
        }
        val times = PrayerTimes(
            Coordinates(context.location.latitude, context.location.longitude),
            DateComponents(year, month, day),
            parameters
        )
        val formatter = SimpleDateFormat("HH:mm", Locale.US)
        return PrayerScheduleValidator.requireValid(
            PrayerSchedule(
                date = context.date,
                locationKey = context.location.key,
                source = "local-adhan",
                prayers = listOf(
                    PrayerTime("Fajr", formatter.format(times.fajr)),
                    PrayerTime("Sunrise", formatter.format(times.sunrise)),
                    PrayerTime("Dhuhr", formatter.format(times.dhuhr)),
                    PrayerTime("Asr", formatter.format(times.asr)),
                    PrayerTime("Maghrib", formatter.format(times.maghrib)),
                    PrayerTime("Isha", formatter.format(times.isha))
                )
            )
        )
    }
}
