package com.zaidan.quraneasy.feature.quran.data.local

import androidx.room.TypeConverter
import com.zaidan.quraneasy.core.model.DownloadState

class Converters {
    @TypeConverter
    fun fromDownloadState(state: DownloadState): String = state.name

    @TypeConverter
    fun toDownloadState(value: String): DownloadState =
        DownloadState.valueOf(value)
}