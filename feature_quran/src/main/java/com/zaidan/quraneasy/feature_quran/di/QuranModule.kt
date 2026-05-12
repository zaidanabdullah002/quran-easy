package com.zaidan.quraneasy.feature_quran.di

import android.content.Context
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import com.zaidan.quraneasy.feature_quran.data.QuranDatabase
import com.zaidan.quraneasy.feature_quran.data.QuranLocalDataSource
import com.zaidan.quraneasy.feature_quran.data.QuranRepository
import com.zaidan.quraneasy.feature_quran.data.QuranRepositoryImpl
import com.zaidan.quraneasy.feature_quran.data.SurahDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object QuranModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): QuranDatabase =
        Room.databaseBuilder(context, QuranDatabase::class.java, "quran_easy.db")
            .addCallback(QuranDatabaseSeeder())
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideSurahDao(database: QuranDatabase): SurahDao = database.surahDao()

    @Provides
    @Singleton
    fun provideLocalDataSource(surahDao: SurahDao): QuranLocalDataSource =
        QuranLocalDataSource(surahDao)

    @Provides
    @Singleton
    fun provideRepository(localDataSource: QuranLocalDataSource): QuranRepository =
        QuranRepositoryImpl(localDataSource)
}

private class QuranDatabaseSeeder : androidx.room.RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        db.execSQL(
            """
            INSERT INTO surah (id, arabicName, englishName, revelationType, ayahCount) VALUES
            (1, 'الفاتحة', 'Al-Fatihah', 'Meccan', 7),
            (2, 'البقرة', 'Al-Baqarah', 'Medinan', 286),
            (3, 'آل عمران', 'Al-Imran', 'Medinan', 200)
            """.trimIndent()
        )
    }
}

