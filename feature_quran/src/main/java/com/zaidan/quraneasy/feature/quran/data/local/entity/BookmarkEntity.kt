package com.zaidan.quraneasy.feature.quran.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "bookmarks",
    indices = [
        Index(value = ["type", "surahNumber"], unique = true),
        Index(value = ["type", "juzNumber"], unique = true)
    ]
)
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: BookmarkType,
    val surahNumber: Int? = null,
    val juzNumber: Int? = null,
    val createdAt: Long = System.currentTimeMillis()
)

enum class BookmarkType {
    SURAH,
    JUZ
}
