package com.zaidan.quraneasy.feature.quran.data.repository

import android.util.Log
import com.zaidan.quraneasy.core.model.DownloadState
import com.zaidan.quraneasy.feature.quran.data.local.QuranLocalDataSource
import com.zaidan.quraneasy.feature.quran.data.local.entity.AyahEntity
import com.zaidan.quraneasy.feature.quran.data.remote.QuranRemoteDataSource
import com.zaidan.quraneasy.feature.quran.data.toUiModel
import com.zaidan.quraneasy.feature.quran.domain.repository.QuranRepository
import com.zaidan.quraneasy.feature.quran.presentation.model.AyahUiModel
import com.zaidan.quraneasy.feature.quran.presentation.model.SurahUiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuranRepositoryImpl @Inject constructor(
    private val localDataSource: QuranLocalDataSource,
    private val remoteDataSource: QuranRemoteDataSource
) : QuranRepository {
    companion object {
        private const val TAG = "QuranRepositoryImpl"
    }

    override fun observeSurahs(): Flow<List<SurahUiModel>> {
        Log.i(TAG, "observeSurahs: called")
        return localDataSource.observeAllSurahs().map { surah ->
            Log.i(TAG, "observeSurahs: $surah")
            surah.map {
                it.toUiModel()
            }
        }
    }

    override fun observeSurah(surahNumber: Int): Flow<List<AyahUiModel>> {
        return localDataSource.observeAyahsBySurah(surahNumber).map { ayah ->
            Log.i(TAG, "observeSurah: $ayah")
            ayah.map {
                it.toUiModel()
            }
        }
    }

    override fun observeJuz(juzNumber: Int): Flow<List<AyahUiModel>> {
        return localDataSource.observeAyahsByJuz(juzNumber).map { ayah ->
            Log.i(TAG, "observeSurah: $ayah")
            ayah.map {
                it.toUiModel()
            }
        }
    }

    override fun observePage(pageNumber: Int): Flow<List<AyahEntity>> {
        TODO("Not yet implemented")
    }

    override suspend fun ensureSurahListDownloaded() {
        val surahCount = localDataSource.getSurahsCount()
        if (surahCount == 114) {
            Log.i(TAG, "ensureSurahListDownloaded: already downloaded")
            return
        }
        try {
            Log.i(TAG, "ensureSurahListDownloaded: downloading")
            val surahs = remoteDataSource.downloadSurahList()
            Log.i(TAG, "ensureSurahListDownloaded: $surahs")
            localDataSource.insertSurahs(surahs)
        } catch (e: Exception) {
            Log.e(TAG, "ensureSurahListDownloaded: failed to download , ${e.message}")
            throw e
        }
    }

    override suspend fun ensureSurahDownloaded(surahNumber: Int) {
        val surah = localDataSource.getSurahByNumber(surahNumber) ?: return
        if (surah.downloadState == DownloadState.DOWNLOADED) {
            Log.i(TAG, "ensureSurahDownloaded: already downloaded $surah")
            return
        }
        try {
            Log.i(TAG, "ensureSurahDownloaded: downloading surahNumber $surahNumber")
            val ayahList = remoteDataSource.downloadSurah(surahNumber)
            localDataSource.insertAyahs(ayahList)
            localDataSource.updateSurahDownloadState(surahNumber, DownloadState.DOWNLOADED)
            Log.i(TAG, "ensureSurahDownloaded: downloaded surahNumber $surahNumber")
        } catch (e: Exception) {
            Log.e(
                TAG,
                "ensureSurahDownloaded: failed to download surahNumber $surahNumber , ${e.message}"
            )
            localDataSource.updateSurahDownloadState(surahNumber, DownloadState.FAILED)
            throw e
        }
    }

    override suspend fun ensureJuzDownloaded(juzNumber: Int) {
        val ayahCount = localDataSource.getAyahCountForJuz(juzNumber)
        if (ayahCount > 0) {
            Log.i(TAG, "ensureJuzDownloaded: already downloaded")
            return
        }
        try {
            Log.i(TAG, "ensureJuzDownloaded: downloading juzNumber $juzNumber")
            val ayahList = remoteDataSource.downloadJuz(juzNumber)
            localDataSource.insertAyahs(ayahList)
            Log.i(TAG, "ensureJuzDownloaded: downloaded juzNumber $juzNumber")
        } catch (e: Exception) {
            Log.e(
                TAG,
                "ensureJuzDownloaded: failed to download juzNumber $juzNumber , ${e.message}"
            )
        }
    }
}