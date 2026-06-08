package com.zaidan.quraneasy.feature.quran.data.di

import android.content.Context
import androidx.room.Room
import com.zaidan.quraneasy.feature.quran.data.local.QuranDatabase
import com.zaidan.quraneasy.feature.quran.data.local.dao.AyahDao
import com.zaidan.quraneasy.feature.quran.data.local.dao.SurahDao
import com.zaidan.quraneasy.feature.quran.data.local.dao.TranslationDao
import com.zaidan.quraneasy.feature.quran.data.local.dao.TranslationMetadataDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideQuranDatabase(@ApplicationContext context: Context): QuranDatabase {
        return Room.databaseBuilder(
            context,
            QuranDatabase::class.java,
            "quran_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideAyahDao(database: QuranDatabase): AyahDao {
        return database.ayahDao
    }

    @Provides
    @Singleton
    fun provideSurahDao(database: QuranDatabase): SurahDao {
        return database.surahDao
    }

    @Provides
    @Singleton
    fun provideTranslationDao(database: QuranDatabase): TranslationDao {
        return database.translationDao
    }

    @Provides
    @Singleton
    fun provideTranslationMetadataDao(database: QuranDatabase): TranslationMetadataDao {
        return database.translationMetadataDao
    }
}
