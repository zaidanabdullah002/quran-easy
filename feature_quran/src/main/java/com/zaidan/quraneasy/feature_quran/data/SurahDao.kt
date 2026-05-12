package com.zaidan.quraneasy.feature_quran.data

import androidx.room.Dao
import androidx.room.Query
import com.zaidan.quraneasy.feature_quran.model.Surah
import kotlinx.coroutines.flow.Flow

@Dao
interface SurahDao {
    @Query("SELECT * FROM surah ORDER BY id ASC")
    fun observeSurahs(): Flow<List<Surah>>
}

