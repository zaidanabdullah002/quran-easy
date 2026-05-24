package com.zaidan.quraneasy.feature.tasbih.data.di

import android.content.Context
import androidx.room.Room
import com.zaidan.quraneasy.feature.tasbih.data.local.TasbihDao
import com.zaidan.quraneasy.feature.tasbih.data.local.TasbihDatabase
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
    fun provideDatabase(@ApplicationContext app: Context): TasbihDatabase {
        return Room.databaseBuilder(
            app,
            TasbihDatabase::class.java,
            "tasbih_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideTasbihDao(db: TasbihDatabase): TasbihDao {
        return db.tasbihDao()
    }

}