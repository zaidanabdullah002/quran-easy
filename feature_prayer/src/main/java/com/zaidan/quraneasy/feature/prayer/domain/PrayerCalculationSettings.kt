package com.zaidan.quraneasy.feature.prayer.domain

import javax.inject.Inject
import javax.inject.Singleton

enum class PrayerCalculationMethod(val apiId: Int) {
    KARACHI(1),
    ISNA(2),
    MUSLIM_WORLD_LEAGUE(3),
    UMM_AL_QURA(4)
}

enum class PrayerAsrSchool(val apiId: Int) {
    SHAFI(0),
    HANAFI(1)
}

data class PrayerCalculationSettings(
    val method: PrayerCalculationMethod = PrayerCalculationMethod.KARACHI,
    val school: PrayerAsrSchool = PrayerAsrSchool.HANAFI
)

@Singleton
class PrayerSettingsProvider @Inject constructor() {
    // One centralized setting is used by local calculation and both HTTP providers.
    // Karachi + Hanafi is the conventional default for the app's South-Asian audience.
    val settings = PrayerCalculationSettings()
}
