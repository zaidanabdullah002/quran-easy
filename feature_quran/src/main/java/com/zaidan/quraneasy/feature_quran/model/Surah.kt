package com.zaidan.quraneasy.feature_quran.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "surah")
data class Surah(
    @PrimaryKey val id: Int,
    val arabicName: String,
    val englishName: String,
    val revelationType: String,
    val ayahCount: Int
)

