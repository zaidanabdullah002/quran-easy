package com.zaidan.quraneasy.feature.prayer.presentation

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class CurrentPrayerProgress(
    val name: String,
    val progress: Float,
    val timeLeftLabel: String
)

fun calculateCurrentPrayerProgress(
    prayers: List<PrayerRowUi>,
    calendar: Calendar = Calendar.getInstance()
): CurrentPrayerProgress? {
    if (prayers.isEmpty() || prayers.any { it.time == "--:--" }) return null

    val nowMinutes = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)
    val prayerMinutes = prayers.mapNotNull { prayer ->
        parsePrayerMinutes(prayer.time)?.let { prayer.name to it }
    }
    if (prayerMinutes.isEmpty()) return null

    val nextPrayer = prayerMinutes.firstOrNull { (_, minutes) -> minutes > nowMinutes }
        ?: prayerMinutes.firstOrNull()
        ?: return null
    val currentPrayer = prayerMinutes.lastOrNull { (_, minutes) -> minutes <= nowMinutes }
        ?: prayerMinutes.lastOrNull()?.let { it.first to (it.second - 24 * 60) }
        ?: return null

    val currentPrayerName = currentPrayer.first
    val currentPrayerMinutes = currentPrayer.second
    val nextPrayerMinutes = nextPrayer.second

    val adjustedNextMinutes = if (nextPrayerMinutes <= currentPrayerMinutes) {
        nextPrayerMinutes + 24 * 60
    } else {
        nextPrayerMinutes
    }
    val adjustedNowMinutes = if (nowMinutes < currentPrayerMinutes) {
        nowMinutes + 24 * 60
    } else {
        nowMinutes
    }

    val segmentDuration = (adjustedNextMinutes - currentPrayerMinutes).coerceAtLeast(1)
    val remaining = (adjustedNextMinutes - adjustedNowMinutes).coerceAtLeast(0)

    return CurrentPrayerProgress(
        name = currentPrayerName,
        progress = remaining.toFloat() / segmentDuration.toFloat(),
        timeLeftLabel = formatDurationLabel(remaining)
    )
}

private fun parsePrayerMinutes(prayerTime: String): Int? {
    val parsed = runCatching {
        SimpleDateFormat("HH:mm", Locale.US).parse(prayerTime)
    }.getOrNull() ?: return null
    val calendar = Calendar.getInstance().apply { time = parsed }
    return calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)
}

private fun formatDurationLabel(totalMinutes: Int): String {
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    return if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
}
