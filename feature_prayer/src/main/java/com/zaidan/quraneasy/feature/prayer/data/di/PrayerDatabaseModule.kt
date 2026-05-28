package com.zaidan.quraneasy.feature.prayer.data.di

import android.content.Context
import androidx.annotation.UiContext
import androidx.room.Room
import com.zaidan.quraneasy.feature.prayer.data.local.PrayerDao
import com.zaidan.quraneasy.feature.prayer.data.local.PrayerDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PrayerDatabaseModule {

    @Provides
    @Singleton
    fun providePrayerDB(
        @ApplicationContext context: Context
    ) : PrayerDataBase {
        return Room.databaseBuilder(
            context,
            PrayerDataBase::class.java,
            "prayer_db"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun providePrayerDao(dataBase: PrayerDataBase) : PrayerDao{
        return dataBase.prayerDao()
    }

}