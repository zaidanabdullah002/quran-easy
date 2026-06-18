package com.zaidan.quraneasy.feature.prayer.data.local.entity

import androidx.room.Entity


@Entity(
    tableName = "prayer_completion",
    primaryKeys = ["date", "name"]
)
data class PrayerCompletionEntity (
    val date: String,
    val name: String,
    val completed: Boolean = false
)