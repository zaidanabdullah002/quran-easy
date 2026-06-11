package com.zaidan.quraneasy.feature.feeling.presentation

import androidx.compose.runtime.Immutable

@Immutable
data class Feeling(
    val id: String,
    val emoji: String,
    val title: String,
    val subtitle: String,
    val verses: List<VerseRef>,
    val category: FeelingCategory = FeelingCategory.Feeling
)

@Immutable
data class VerseRef(
    val surah: Int,
    val ayah: Int
)

enum class FeelingCategory {
    Feeling,
    QuranDua
}

data class FeelingWithVerses(
    val feeling: Feeling,
    val verses: List<FeelingVerseUiModel>
)

@Immutable
data class FeelingVerseUiModel(
    val surahNumber: Int,
    val numberInSurah: Int,
    val arabicText: String,
    val translation: String? = null
)
