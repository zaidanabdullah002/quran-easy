package com.zaidan.quraneasy.feature.quran.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "translations",
    primaryKeys = ["surahNumber", "numberInSurah", "translationId"],
    indices = [
        Index("juzNumber"),
        Index("pageNumber")
    ]
)
data class TranslationEntity(
    val surahNumber: Int,
    val numberInSurah: Int,
    val juzNumber: Int,
    val pageNumber: Int,
    // e.g. "en.sahih", "en.pickthall", "ur.jalandhry"
    val translationId: String,
    val languageCode: String,
    val text: String
)