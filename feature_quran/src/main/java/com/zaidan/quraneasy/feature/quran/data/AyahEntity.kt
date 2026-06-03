package com.zaidan.quraneasy.feature.quran.data

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "ayahs")
data class AyahEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val surahNumber: Int,
    val ayahNumber: Int,
    val juzNumber: Int,
    val arabicText: String,
    val translation: String,
    val englishSurahName: String,
    val arabicSurahName: String
)