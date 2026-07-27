package com.zaidan.quraneasy.feature.prayer.domain

import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class PrayerLocation(
    val latitude: Double,
    val longitude: Double
) {
    val key: String = String.format(Locale.US, "%.3f,%.3f", latitude, longitude)
}

data class PrayerContext(
    val date: String,
    val location: PrayerLocation
) {
    val key: String = "$date@${location.key}"
}

enum class PrayerContextChange {
    INITIAL,
    DATE,
    LOCATION,
    DATE_AND_LOCATION,
    NONE
}

object PrayerContextChangeDetector {
    fun detect(
        previous: PrayerContext?,
        current: PrayerContext,
        locationThresholdMeters: Double = 10_000.0
    ): PrayerContextChange {
        if (previous == null) return PrayerContextChange.INITIAL

        val dateChanged = previous.date != current.date
        val locationChanged = distanceMeters(previous.location, current.location) >= locationThresholdMeters

        return when {
            dateChanged && locationChanged -> PrayerContextChange.DATE_AND_LOCATION
            dateChanged -> PrayerContextChange.DATE
            locationChanged -> PrayerContextChange.LOCATION
            else -> PrayerContextChange.NONE
        }
    }

    private fun distanceMeters(first: PrayerLocation, second: PrayerLocation): Double {
        val earthRadiusMeters = 6_371_000.0
        val latitudeDelta = Math.toRadians(second.latitude - first.latitude)
        val longitudeDelta = Math.toRadians(second.longitude - first.longitude)
        val firstLatitude = Math.toRadians(first.latitude)
        val secondLatitude = Math.toRadians(second.latitude)
        val a = sin(latitudeDelta / 2) * sin(latitudeDelta / 2) +
            cos(firstLatitude) * cos(secondLatitude) *
            sin(longitudeDelta / 2) * sin(longitudeDelta / 2)
        return earthRadiusMeters * 2 * atan2(sqrt(a), sqrt(1 - a))
    }
}
