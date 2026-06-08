package com.zaidan.quraneasy.feature.quran.presentation.model

import com.zaidan.quraneasy.core.model.DownloadState

data class SurahUiModel(
    val number: Int,
    val englishName: String,
    val translation: String,
    val verses: Int,
    val arabicName: String,
    val downloadedState: DownloadState = DownloadState.NOT_DOWNLOADED
)