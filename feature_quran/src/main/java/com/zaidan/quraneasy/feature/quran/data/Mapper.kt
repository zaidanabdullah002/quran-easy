package com.zaidan.quraneasy.feature.quran.data

import com.zaidan.quraneasy.feature.quran.data.local.entity.AyahEntity
import com.zaidan.quraneasy.feature.quran.data.local.entity.SurahEntity
import com.zaidan.quraneasy.feature.quran.data.remote.dto.JuzAyahDto
import com.zaidan.quraneasy.feature.quran.data.remote.dto.JuzDto
import com.zaidan.quraneasy.feature.quran.data.remote.dto.SurahAyahDto
import com.zaidan.quraneasy.feature.quran.data.remote.dto.SurahDto
import com.zaidan.quraneasy.feature.quran.data.remote.dto.SurahTitleDto
import com.zaidan.quraneasy.feature.quran.presentation.model.AyahUiModel
import com.zaidan.quraneasy.feature.quran.presentation.model.SurahUiModel

fun SurahDto.toEntities(): List<AyahEntity> {
    return ayahs.map {
        it.toEntity(number)
    }
}

fun SurahAyahDto.toEntity(surahNumber: Int): AyahEntity {
    return AyahEntity(
        surahNumber = surahNumber,
        numberInSurah = numberInSurah,
        juzNumber = juz,
        pageNumber = page,
        arabicText = text
    )
}

fun JuzDto.toEntities(): List<AyahEntity> {
    return ayahs.map {
        it.toEntity()
    }
}

fun JuzAyahDto.toEntity(): AyahEntity {
    return AyahEntity(
        surahNumber = surah.number,
        numberInSurah = numberInSurah,
        juzNumber = juz,
        pageNumber = page,
        arabicText = text
    )
}


fun SurahTitleDto.toEntity(): SurahEntity{
    return SurahEntity(
        number = number,
        englishName = englishName,
        arabicName = name,
        translation = englishNameTranslation,
        ayahCount = numberOfAyahs
    )
}

fun SurahEntity.toUiModel() : SurahUiModel {
    return SurahUiModel(
        number = number,
        englishName = englishName,
        arabicName = arabicName,
        translation = translation,
        verses = ayahCount,
        downloadedState = downloadState
    )
}

fun AyahEntity.toUiModel() : AyahUiModel{
    return AyahUiModel(
        surahNumber = surahNumber,
        numberInSurah = numberInSurah,
        arabicText = arabicText,
        translation = ""
    )
}

