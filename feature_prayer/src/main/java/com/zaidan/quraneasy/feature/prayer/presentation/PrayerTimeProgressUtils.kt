package com.zaidan.quraneasy.feature.prayer.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import kotlinx.coroutines.delay
import java.util.Calendar

data class CurrentPrayerProgress(val name: String, val progress: Float, val timeLeftLabel: String)

/** Clock times are interpreted in the same device zone used by the schedule provider. */
fun calculateCurrentPrayerProgress(
    prayers: List<PrayerRowUi>,
    calendar: Calendar = Calendar.getInstance(),
): CurrentPrayerProgress? {
    val trackable = prayers.filter { it.trackable }
    if (trackable.isEmpty()) return null
    val times = trackable.map { prayer ->
        val minutes = parsePrayerMinutes(prayer.time) ?: return null
        prayer.name to minutes
    }.sortedBy { it.second }
    val now = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)
    val next = times.firstOrNull { it.second > now }
        ?: times.first().let { it.first to it.second + 1440 }
    val previous = times.lastOrNull { it.second <= now }?.second ?: (times.last().second - 1440)
    val remaining = next.second - now
    return CurrentPrayerProgress(
        name = next.first,
        progress = (remaining.toFloat() / (next.second - previous).coerceAtLeast(1)).coerceIn(0f, 1f),
        timeLeftLabel = if (remaining >= 60) "${remaining / 60}h ${remaining % 60}m" else "${remaining}m",
    )
}

@Composable
fun rememberPrayerProgress(prayers: List<PrayerRowUi>): CurrentPrayerProgress? {
    val progress by produceState(calculateCurrentPrayerProgress(prayers), prayers) {
        while (true) {
            value = calculateCurrentPrayerProgress(prayers)
            delay(30_000)
        }
    }
    return progress
}

private fun parsePrayerMinutes(time: String): Int? {
    if (!Regex("\\d{2}:\\d{2}").matches(time)) return null
    val hour = time.substring(0, 2).toIntOrNull() ?: return null
    val minute = time.substring(3, 5).toIntOrNull() ?: return null
    return if (hour in 0..23 && minute in 0..59) hour * 60 + minute else null
}
