package com.zaidan.quraneasy.feature.quran.data.remote

import com.zaidan.quraneasy.feature.quran.data.remote.dto.JuzApiResponse
import com.zaidan.quraneasy.feature.quran.data.remote.dto.AyahTranslationResponse
import com.zaidan.quraneasy.feature.quran.data.remote.dto.SurahApiResponse
import com.zaidan.quraneasy.feature.quran.data.remote.dto.SurahListResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface QuranApiService {
    @GET("v1/ayah/{reference}/{edition}")
    suspend fun getAyahTranslation(
        @Path("reference") reference: String,
        @Path("edition") edition: String
    ): AyahTranslationResponse

    @GET("v1/surah/{surahNumber}")
    suspend fun getSurah(
        @Path("surahNumber") surahNumber: Int
    ): SurahApiResponse

    @GET("v1/juz/{juzNumber}/quran-uthmani")
    suspend fun getJuz(
        @Path("juzNumber") juzNumber: Int
    ): JuzApiResponse

    @GET("v1/surah")
    suspend fun getSurahList(): SurahListResponse

}
