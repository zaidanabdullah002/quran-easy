package com.zaidan.quraneasy.feature.quran.data.local

import androidx.room.TypeConverter
import com.zaidan.quraneasy.core.model.DownloadState
import com.zaidan.quraneasy.feature.quran.data.local.entity.BookmarkType

class Converters {
    @TypeConverter
    fun fromDownloadState(state: DownloadState): String = state.name

    @TypeConverter
    fun toDownloadState(value: String): DownloadState =
        DownloadState.valueOf(value)

    @TypeConverter
    fun fromBookmarkType(type: BookmarkType): String = type.name

    @TypeConverter
    fun toBookmarkType(value: String): BookmarkType =
        BookmarkType.valueOf(value)
}
