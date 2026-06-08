package com.zaidan.quraneasy.feature.quran.data.remote

import com.zaidan.quraneasy.feature.quran.data.local.entity.AyahEntity
import com.zaidan.quraneasy.feature.quran.data.local.entity.SurahEntity
import com.zaidan.quraneasy.feature.quran.data.toEntities
import com.zaidan.quraneasy.feature.quran.data.toEntity
import javax.inject.Inject

class QuranRemoteDataSource @Inject constructor(
    private val quranApiService: QuranApiService
) {
    suspend fun downloadSurah(surahNumber: Int): List<AyahEntity> {
        //gets SurahApiResponse -> SurahDto -> List<SurahAyahDto> -> List<AyahEntity>
        return quranApiService.getSurah(surahNumber).data.toEntities()
    }

    suspend fun downloadJuz(juzNumber: Int): List<AyahEntity> {
        //gets JuzApiResponse -> JuzDto -> List<JuzAyahDto> -> List<AyahEntity>
        return quranApiService.getJuz(juzNumber).data.toEntities()
    }

    suspend fun downloadSurahList(): List<SurahEntity> {
        //gets SurahListResponse -> List<SurahTitleDto> -> List<SurahEntity>
        return quranApiService.getSurahList().data.map { it.toEntity() }
    }
}