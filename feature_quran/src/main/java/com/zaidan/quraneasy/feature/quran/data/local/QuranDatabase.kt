package com.zaidan.quraneasy.feature.quran.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.zaidan.quraneasy.feature.quran.data.local.dao.AyahDao
import com.zaidan.quraneasy.feature.quran.data.local.dao.BookmarkDao
import com.zaidan.quraneasy.feature.quran.data.local.dao.SurahDao
import com.zaidan.quraneasy.feature.quran.data.local.dao.TranslationDao
import com.zaidan.quraneasy.feature.quran.data.local.dao.TranslationMetadataDao
import com.zaidan.quraneasy.feature.quran.data.local.entity.AyahEntity
import com.zaidan.quraneasy.feature.quran.data.local.entity.BookmarkEntity
import com.zaidan.quraneasy.feature.quran.data.local.entity.SurahEntity
import com.zaidan.quraneasy.feature.quran.data.local.entity.TranslationEntity
import com.zaidan.quraneasy.feature.quran.data.local.entity.TranslationMetadataEntity

@Database(
    entities = [
        AyahEntity::class,
        BookmarkEntity::class,
        SurahEntity::class,
        TranslationEntity::class,
        TranslationMetadataEntity::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class QuranDatabase : RoomDatabase(){
    abstract val ayahDao: AyahDao
    abstract val bookmarkDao: BookmarkDao
    abstract val surahDao: SurahDao
    abstract val translationDao: TranslationDao
    abstract val translationMetadataDao: TranslationMetadataDao
}
