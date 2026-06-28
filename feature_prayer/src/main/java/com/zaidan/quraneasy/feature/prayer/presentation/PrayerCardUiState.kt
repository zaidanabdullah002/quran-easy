package com.zaidan.quraneasy.feature.prayer.presentation

import com.zaidan.quraneasy.feature.prayer.domain.Prayer

data class PrayerCardUiState(
    val cardMode: PrayerCardMode = PrayerCardMode.NoPermission,
    val prayers: List<PrayerRowUi> = PrayerRowUi.placeholderList(),
    val locationLabel: String? = null,
    val banner: String? = null,
    val actionLabel: String? = null
)

enum class PrayerCardMode {
    NoPermission,
    Loading,
    Ready,
    ReadyOfflineCache,
    Unavailable
}

data class PrayerRowUi(
    val name: String,
    val time: String,
    val completed: Boolean,
    val enabled: Boolean,
    val trackable: Boolean = true
) {
    companion object {
        fun placeholderList() = listOf(
            PrayerRowUi("Fajr", "--:--", false, false),
            PrayerRowUi("Sunrise", "--:--", false, false, trackable = false),
            PrayerRowUi("Dhuhr", "--:--", false, false),
            PrayerRowUi("Asr", "--:--", false, false),
            PrayerRowUi("Maghrib", "--:--", false, false),
            PrayerRowUi("Isha", "--:--", false, false)
        )
    }
}
