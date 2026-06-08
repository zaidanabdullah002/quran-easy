package com.zaidan.quraneasy.feature.quran.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ayah",
    primaryKeys = ["surahNumber", "numberInSurah"],
    indices = [
        Index("juzNumber"),
        Index("pageNumber")
    ]
)
data class AyahEntity(
    val surahNumber: Int,
    val numberInSurah: Int,
    val juzNumber: Int,
    val pageNumber: Int,
    val arabicText: String
)