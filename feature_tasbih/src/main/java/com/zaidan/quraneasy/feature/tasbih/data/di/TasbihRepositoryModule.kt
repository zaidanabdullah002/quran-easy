package com.zaidan.quraneasy.feature.tasbih.data.di

import com.zaidan.quraneasy.feature.tasbih.data.repository.TasbihRepositoryImpl
import com.zaidan.quraneasy.feature.tasbih.domain.repository.TasbihRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TasbihRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTasbihRepository(
        impl: TasbihRepositoryImpl
    ): TasbihRepository
}
