package com.zaidan.quraneasy.feature.prayer.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "prayer_entity")
data class PrayerEntity (
    @PrimaryKey
    val name: String,
    val time: String,
    val completed: Boolean = false
)