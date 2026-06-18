package com.zaidan.quraneasy.feature.prayer.data.di

import com.zaidan.quraneasy.feature.prayer.data.location.FusedLocationObserver
import com.zaidan.quraneasy.feature.prayer.data.location.LocationObserver
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PrayerLocationModule {

    @Binds
    @Singleton
    abstract fun bindLocationObserver(
        impl: FusedLocationObserver
    ): LocationObserver
}
