package com.zaidan.quraneasy.feature.quran.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.zaidan.quraneasy.core.model.DownloadState

@Entity(tableName = "translation_metadata")
data class TranslationMetadataEntity(
    @PrimaryKey
    val translationId: String,
    val languageCode: String,
    val displayName: String,
    val author: String,
    val downloadState: DownloadState = DownloadState.NOT_DOWNLOADED
)