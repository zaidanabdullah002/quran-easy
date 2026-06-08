package com.zaidan.quraneasy.feature.quran.data.remote.dto

data class SurahApiResponse(
    val code: Int,
    val status: String,
    val data: SurahDto
)

data class SurahDto(
    val number: Int,
    val name: String,
    val englishName: String,
    val englishNameTranslation: String,
    val revelationType: String,
    val numberOfAyahs: Int,
    val ayahs: List<SurahAyahDto>
)

data class SurahAyahDto(
    val number: Int,
    val text: String,
    val numberInSurah: Int,
    val juz: Int,
    val manzil: Int,
    val page: Int,
    val ruku: Int,
    val hizbQuarter: Int,
    val sajda: Boolean
)