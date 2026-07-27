package com.zaidan.quraneasy.feature.prayer.data.di

import com.zaidan.quraneasy.feature.prayer.data.remote.NetworkConstants.BASE_URL
import com.zaidan.quraneasy.feature.prayer.data.remote.IslamicAppApiService
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PrayerNetworkModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder().build()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().build()
    }

    @Provides
    @Singleton
    fun provideIslamicAppApiService(
        okHttpClient: OkHttpClient,
        moshi: Moshi
    ): IslamicAppApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .build()
            .create(IslamicAppApiService::class.java)
    }
}
