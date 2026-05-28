package com.zaidan.quraneasy.feature.prayer.data.di

import com.zaidan.quraneasy.feature.prayer.data.repository.PrayerRepositoryImpl
import com.zaidan.quraneasy.feature.prayer.domain.repository.PrayerRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PrayerRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindPrayerRepository(
        impl: PrayerRepositoryImpl
    ) : PrayerRepository
}