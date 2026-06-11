package com.zaidan.quraneasy.feature.quran.data.di

import com.zaidan.quraneasy.feature.quran.data.remote.NetworkConstants
import com.zaidan.quraneasy.feature.quran.data.remote.QuranApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.zaidan.quraneasy.feature.quran.data.remote.SajdaAdapter
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideQuranApiService(): QuranApiService {
        val moshi = Moshi.Builder()
            .add(SajdaAdapter())
            .addLast(KotlinJsonAdapterFactory())
            .build()

        return Retrofit.Builder()
            .baseUrl(NetworkConstants.CLOUD_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(QuranApiService::class.java)
    }
}
