package com.zaidan.quraneasy.feature_quran.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ayah")
data class Ayah(
    @PrimaryKey val id: Int,
    val surahId: Int,
    val numberInSurah: Int,
    val arabicText: String,
    val translationText: String
)

