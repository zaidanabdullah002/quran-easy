package com.zaidan.quraneasy.feature.quran.data.remote.dto

data class SurahListResponse(
    val code: Int,
    val status: String,
    val data: List<SurahTitleDto>
)

data class SurahTitleDto(
    val number: Int,
    val name: String,
    val englishName: String,
    val englishNameTranslation: String,
    val numberOfAyahs: Int,
    val revelationType: String
)