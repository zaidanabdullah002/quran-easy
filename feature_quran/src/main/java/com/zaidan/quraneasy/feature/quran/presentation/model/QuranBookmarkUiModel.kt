package com.zaidan.quraneasy.feature.quran.presentation.model

sealed interface QuranBookmarkUiModel {
    val id: Long
    val createdAt: Long

    data class SurahBookmark(
        override val id: Long,
        override val createdAt: Long,
        val surahNumber: Int,
        val englishName: String,
        val arabicName: String,
        val translation: String
    ) : QuranBookmarkUiModel

    data class JuzBookmark(
        override val id: Long,
        override val createdAt: Long,
        val juzNumber: Int
    ) : QuranBookmarkUiModel
}
