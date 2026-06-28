package com.zaidan.quraneasy.feature.quran.data.remote.dto

data class AyahTranslationResponse(
    val code: Int,
    val status: String,
    val data: AyahTranslationDto
)

data class AyahTranslationDto(
    val numberInSurah: Int,
    val juz: Int,
    val page: Int,
    val text: String,
    val edition: TranslationEditionDto
)

data class TranslationEditionDto(
    val identifier: String,
    val language: String,
    val englishName: String,
    val name: String
)
