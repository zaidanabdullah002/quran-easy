package com.zaidan.quraneasy.feature.prayer.data.local.entity

import androidx.room.Entity

@Entity(
    tableName = "prayer_schedule",
    primaryKeys = ["date", "locationKey", "name"]
)
data class PrayerScheduleEntity(
    val date: String,
    val locationKey: String,
    val locationLabel: String,
    val name: String,
    val time: String
)
