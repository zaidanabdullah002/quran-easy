package com.zaidan.quraneasy.feature.feeling.presentation

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class Feeling(
    val id: String,
    val emoji: String,
    val title: String,
    val subtitle: String,
    val verses: List<VerseRef>,
    val previewTranslation: String? = null,
    val accent: String? = null,
    val artworkKey: String? = null,
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

fun String?.toColorOrNull(): Color? = runCatching {
    if (isNullOrBlank()) null else Color(android.graphics.Color.parseColor(this))
}.getOrNull()
