package com.zaidan.quraneasy.feature.quran.data.local

import com.zaidan.quraneasy.core.model.DownloadState
import com.zaidan.quraneasy.feature.quran.data.local.dao.AyahDao
import com.zaidan.quraneasy.feature.quran.data.local.dao.BookmarkDao
import com.zaidan.quraneasy.feature.quran.data.local.dao.SurahDao
import com.zaidan.quraneasy.feature.quran.data.local.entity.AyahEntity
import com.zaidan.quraneasy.feature.quran.data.local.entity.BookmarkEntity
import com.zaidan.quraneasy.feature.quran.data.local.entity.SurahEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class QuranLocalDataSource @Inject constructor(
    private val ayahDao: AyahDao,
    private val bookmarkDao: BookmarkDao,
    private val surahDao: SurahDao,
) {
    // Ayah
    fun observeAyahsBySurah(
        surahNumber: Int,
    ): Flow<List<AyahEntity>> =
        ayahDao.observeAyahBySurah(surahNumber)

    fun observeAyahsByJuz(
        juzNumber: Int,
    ): Flow<List<AyahEntity>> =
        ayahDao.observeAyahByJuz(juzNumber)

    fun observeAyahsByPage(
        pageNumber: Int,
    ): Flow<List<AyahEntity>> =
        ayahDao.observeAyahByPage(pageNumber)

    suspend fun insertAyahs(
        ayahs: List<AyahEntity>,
    ) {
        ayahDao.insertAyahList(ayahs)
    }

    suspend fun getAyahCountForSurah(
        surahNumber: Int,
    ): Int =
        ayahDao.getAyahCountForSurah(surahNumber)

    suspend fun getAyahCountForJuz(
        juzNumber: Int,
    ): Int =
        ayahDao.getAyahCountForJuz(juzNumber)

    suspend fun getAyahCountForPage(
        pageNumber: Int,
    ): Int =
        ayahDao.getAyahCountPagePage(pageNumber)
    // Surah
    fun observeAllSurahs(): Flow<List<SurahEntity>> =
        surahDao.observeAllSurahs()

    suspend fun insertSurahs(
        surahs: List<SurahEntity>,
    ) {
        surahDao.insertSurahs(surahs)
    }

    suspend fun getSurahsCount(): Int =
        surahDao.getSurahsCount()

    suspend fun getSurahByNumber(
        surahNumber: Int,
    ): SurahEntity? =
        surahDao.getSurahByNumber(surahNumber)

    suspend fun getSurahDownloadState(
        surahNumber: Int,
    ): DownloadState? =
        surahDao.getSurahDownloadedState(surahNumber)

    suspend fun updateSurahDownloadState(
        surahNumber: Int,
        downloadState: DownloadState,
    ) {
        surahDao.updateSurahDownloadState(
            surahNumber = surahNumber,
            downloadState = downloadState,
        )
    }

    fun observeBookmarks(): Flow<List<BookmarkEntity>> =
        bookmarkDao.observeBookmarks()

    suspend fun insertBookmark(bookmark: BookmarkEntity) {
        bookmarkDao.insertBookmark(bookmark)
    }

    suspend fun deleteSurahBookmark(surahNumber: Int) {
        bookmarkDao.deleteSurahBookmark(surahNumber = surahNumber)
    }

    suspend fun deleteJuzBookmark(juzNumber: Int) {
        bookmarkDao.deleteJuzBookmark(juzNumber = juzNumber)
    }

    fun isSurahBookmarked(surahNumber: Int): Flow<Boolean> =
        bookmarkDao.isSurahBookmarked(surahNumber = surahNumber)

    fun isJuzBookmarked(juzNumber: Int): Flow<Boolean> =
        bookmarkDao.isJuzBookmarked(juzNumber = juzNumber)
}
