package com.zaidan.quraneasy.feature.quran.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.IGNORE
import androidx.room.Query
import androidx.room.Transaction
import com.zaidan.quraneasy.feature.quran.data.local.entity.AyahEntity
import com.zaidan.quraneasy.feature.quran.data.local.entity.DownloadedJuzEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AyahDao {

    @Query("SELECT * FROM ayah WHERE surahNumber = :surahNumber ORDER BY numberInSurah")
    fun observeAyahBySurah(surahNumber: Int): Flow<List<AyahEntity>>

    @Query("SELECT * FROM ayah WHERE juzNumber = :juzNumber ORDER BY surahNumber ASC, numberInSurah ASC")
    fun observeAyahByJuz(juzNumber: Int): Flow<List<AyahEntity>>

    @Query("SELECT * FROM ayah WHERE pageNumber = :pageNumber ORDER BY surahNumber ASC, numberInSurah ASC")
    fun observeAyahByPage(pageNumber: Int): Flow<List<AyahEntity>>

    @Insert(onConflict = IGNORE)
    suspend fun insertAyahList(ayahList: List<AyahEntity>)

    @Insert(onConflict = IGNORE)
    suspend fun insertDownloadedJuz(downloadedJuz: DownloadedJuzEntity)

    @Transaction
    suspend fun insertCompleteJuz(
        ayahList: List<AyahEntity>,
        juzNumber: Int,
    ) {
        insertAyahList(ayahList)
        insertDownloadedJuz(DownloadedJuzEntity(juzNumber))
    }

    @Query("SELECT COUNT(*) FROM ayah WHERE surahNumber = :surahNumber")
    suspend fun getAyahCountForSurah(surahNumber: Int): Int

    @Query("SELECT EXISTS(SELECT 1 FROM downloaded_juz WHERE juzNumber = :juzNumber)")
    suspend fun isJuzDownloaded(juzNumber: Int): Boolean

    @Query("SELECT COUNT(*) FROM ayah WHERE pageNumber = :pageNumber")
    suspend fun getAyahCountPagePage(pageNumber: Int): Int

}
