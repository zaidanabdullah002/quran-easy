package com.zaidan.quraneasy.feature.quran.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class JuzApiResponse(
    val code: Int,
    val status: String,
    val data: JuzDto
)

@JsonClass(generateAdapter = true)
data class JuzDto(
    val number: Int,
    val ayahs: List<JuzAyahDto>
)

@JsonClass(generateAdapter = true)
data class JuzAyahDto(
    val number: Int,
    val text: String,
    val surah: SurahSummaryDto,
    val numberInSurah: Int,
    val juz: Int,
    val manzil: Int,
    val page: Int,
    val ruku: Int,
    val hizbQuarter: Int,
    val sajda: SajdaDto?
)

@JsonClass(generateAdapter = true)
data class SurahSummaryDto(
    val number: Int,
    val name: String,
    val englishName: String,
    val englishNameTranslation: String,
    val revelationType: String,
    val numberOfAyahs: Int
)
