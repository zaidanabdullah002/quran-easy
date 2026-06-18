package com.zaidan.quraneasy.feature.prayer.data.di

import com.zaidan.quraneasy.feature.prayer.data.remote.NetworkConstants.CLOUD_BASE_URL
import com.zaidan.quraneasy.feature.prayer.data.remote.PrayerTimingsApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PrayerNetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            // Level.BODY shows URL, Headers, and the JSON response content
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun providePrayerTimingsApiService(okHttpClient: OkHttpClient): PrayerTimingsApiService {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        return Retrofit.Builder()
            .baseUrl(CLOUD_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient) // Attach the client with logging
            .build()
            .create(PrayerTimingsApiService::class.java)
    }
}
