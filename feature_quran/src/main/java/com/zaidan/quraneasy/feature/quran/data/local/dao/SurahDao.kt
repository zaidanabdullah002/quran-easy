package com.zaidan.quraneasy.feature.quran.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.zaidan.quraneasy.core.model.DownloadState
import com.zaidan.quraneasy.feature.quran.data.local.entity.SurahEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SurahDao {
    @Query("SELECT * FROM surahs ORDER BY number ASC")
    fun observeAllSurahs(): Flow<List<SurahEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSurahs(surahs: List<SurahEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSurah(surah: SurahEntity)

    @Query("SELECT COUNT(*) FROM surahs")
    suspend fun getSurahsCount(): Int

    @Query("SELECT * FROM surahs WHERE number = :surahNumber")
    suspend fun getSurahByNumber(surahNumber: Int): SurahEntity?

    @Query("SELECT downloadState FROM surahs WHERE number = :surahNumber")
    suspend fun getSurahDownloadedState(surahNumber: Int): DownloadState?

    @Query("UPDATE surahs SET downloadState = :downloadState WHERE number = :surahNumber")
    suspend fun updateSurahDownloadState(surahNumber: Int, downloadState: DownloadState)

}