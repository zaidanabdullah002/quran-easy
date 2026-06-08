package com.zaidan.quraneasy.feature.quran.data.di

import com.zaidan.quraneasy.feature.quran.data.repository.QuranRepositoryImpl
import com.zaidan.quraneasy.feature.quran.domain.repository.QuranRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class QuranRepositoryModule {

    @Binds
    abstract fun bindQuranRepository(
        impl: QuranRepositoryImpl
    ): QuranRepository
}
