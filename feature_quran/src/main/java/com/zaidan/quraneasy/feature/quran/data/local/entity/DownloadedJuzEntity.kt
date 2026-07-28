package com.zaidan.quraneasy.feature.quran.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "downloaded_juz")
data class DownloadedJuzEntity(
    @PrimaryKey
    val juzNumber: Int,
)
