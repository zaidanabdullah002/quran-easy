package com.zaidan.quraneasy.feature.quran.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SurahApiResponse(
    val code: Int,
    val status: String,
    val data: SurahDto
)

@JsonClass(generateAdapter = true)
data class SurahDto(
    val number: Int,
    val name: String,
    val englishName: String,
    val englishNameTranslation: String,
    val revelationType: String,
    val numberOfAyahs: Int,
    val ayahs: List<SurahAyahDto>
)

@JsonClass(generateAdapter = true)
data class SurahAyahDto(
    val number: Int,
    val text: String,
    val numberInSurah: Int,
    val juz: Int,
    val manzil: Int,
    val page: Int,
    val ruku: Int,
    val hizbQuarter: Int,
    val sajda: SajdaDto? //(critical) false or object
)

@JsonClass(generateAdapter = true)
data class SajdaDto(
    val id: Int = 0,
    val recommended: Boolean = false,
    val obligatory: Boolean = false
)
