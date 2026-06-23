package com.zaidan.quraneasy.feature.quran.domain.repository

import com.zaidan.quraneasy.feature.quran.data.local.entity.AyahEntity
import com.zaidan.quraneasy.feature.quran.presentation.model.AyahUiModel
import com.zaidan.quraneasy.feature.quran.presentation.model.QuranBookmarkUiModel
import com.zaidan.quraneasy.feature.quran.presentation.model.SurahUiModel
import kotlinx.coroutines.flow.Flow


interface QuranRepository {
    fun observeSurahs(): Flow<List<SurahUiModel>>

    fun observeSurah(surahNumber: Int,): Flow<List<AyahUiModel>>

    fun observeJuz(juzNumber: Int): Flow<List<AyahUiModel>>

    fun observeBookmarks(): Flow<List<QuranBookmarkUiModel>>

    fun isSurahBookmarked(surahNumber: Int): Flow<Boolean>

    fun isJuzBookmarked(juzNumber: Int): Flow<Boolean>

    fun observePage(pageNumber: Int): Flow<List<AyahEntity>>

    suspend fun ensureSurahListDownloaded()

    suspend fun ensureSurahDownloaded(surahNumber: Int)

    suspend fun ensureJuzDownloaded(juzNumber: Int)

    suspend fun addSurahBookmark(surahNumber: Int)

    suspend fun addJuzBookmark(juzNumber: Int)

    suspend fun removeSurahBookmark(surahNumber: Int)

    suspend fun removeJuzBookmark(juzNumber: Int)
} 
