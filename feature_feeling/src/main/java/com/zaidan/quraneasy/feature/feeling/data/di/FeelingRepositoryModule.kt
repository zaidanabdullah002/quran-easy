package com.zaidan.quraneasy.feature.feeling.data.di

import com.zaidan.quraneasy.feature.feeling.data.FeelingRepository
import com.zaidan.quraneasy.feature.feeling.data.FeelingRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FeelingRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindFeelingRepository(
        impl: FeelingRepositoryImpl
    ): FeelingRepository
}

