package com.zaidan.quraneasy.di

import android.content.Context
import com.zaidan.quraneasy.core.settings.SettingsRepository
import com.zaidan.quraneasy.core.settings.createSettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SettingsModule {
    @Provides
    @Singleton
    fun provideSettingsRepository(@ApplicationContext context: Context): SettingsRepository =
        context.createSettingsRepository()
}
