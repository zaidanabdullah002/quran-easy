package com.zaidan.quraneasy.feature_quran.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.zaidan.quraneasy.feature_quran.model.Ayah
import com.zaidan.quraneasy.feature_quran.model.Surah

@Database(entities = [Surah::class, Ayah::class], version = 1, exportSchema = false)
abstract class QuranDatabase : RoomDatabase() {
    abstract fun surahDao(): SurahDao
}

