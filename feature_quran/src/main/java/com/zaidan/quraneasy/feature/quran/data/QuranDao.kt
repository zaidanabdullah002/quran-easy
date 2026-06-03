package com.zaidan.quraneasy.feature.quran.data

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface QuranDao {

    @Query("SELECT * FROM ayahs WHERE surahNumber = :num" +
            " ORDER BY ayahNumber ASC")
    fun getAyahsBySurah(num: Int) : Flow<List<AyahEntity>>

    @Query("SELECT * FROM ayahs WHERE juzNumber = :juzNumber" +
            " ORDER BY surahNumber, ayahNumber ASC")
    fun getAyahsByJuz(juzNumber: Int) : Flow<List<AyahEntity>>

    @Query("SELECT COUNT(*) FROM ayahs")
    suspend fun getAyahCount() : Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(ayahs: List<AyahEntity>)

}