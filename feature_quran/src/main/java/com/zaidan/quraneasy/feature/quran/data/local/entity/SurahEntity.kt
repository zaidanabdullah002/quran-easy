package com.zaidan.quraneasy.feature.quran.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.zaidan.quraneasy.core.model.DownloadState

@Entity(tableName = "surahs")
data class SurahEntity(
    @PrimaryKey
    val number: Int,
    val englishName: String,
    val arabicName: String,
    val translation: String,
    val ayahCount: Int,
    val downloadState: DownloadState = DownloadState.NOT_DOWNLOADED
)