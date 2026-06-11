package com.zaidan.quraneasy.feature.feeling.data

import com.zaidan.quraneasy.feature.feeling.presentation.Feeling
import com.zaidan.quraneasy.feature.feeling.presentation.FeelingCategory
import com.zaidan.quraneasy.feature.feeling.presentation.FeelingDetailUiState
import com.zaidan.quraneasy.feature.feeling.presentation.FeelingEntry
import com.zaidan.quraneasy.feature.feeling.presentation.FeelingVerseUiModel
import com.zaidan.quraneasy.feature.feeling.presentation.VerseRef
import com.zaidan.quraneasy.feature.quran.domain.usecase.QuranUseCasesData
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeelingRepositoryImpl @Inject constructor(
    private val localDataSource: FeelingLocalDataSource,
    private val quranUseCases: QuranUseCasesData
) : FeelingRepository {

    override suspend fun getHomeFeelings(): List<Feeling> {
        return localDataSource.loadCatalog()
            .filter { it.category == FeelingCategory.Feeling || it.category == FeelingCategory.QuranDua }
            .map { it.toFeeling() }
    }

    override suspend fun getFeelingDetail(feelingId: String): FeelingDetailUiState {
        val catalog = localDataSource.loadCatalog()
        val selected = catalog.firstOrNull { it.id == feelingId } ?: catalog.first()
        return FeelingDetailUiState(
            isLoading = false,
            feeling = selected.toFeeling(),
            verses = loadVerses(selected.verses)
        )
    }

    private suspend fun loadVerses(verseRefs: List<VerseRef>): List<FeelingVerseUiModel> {
        return verseRefs.mapNotNull { verseRef ->
            val ayahs = quranUseCases.observeSurah(verseRef.surah).first()
            val match = ayahs.firstOrNull { it.numberInSurah == verseRef.ayah } ?: return@mapNotNull null
            FeelingVerseUiModel(
                surahNumber = match.surahNumber,
                numberInSurah = match.numberInSurah,
                arabicText = match.arabicText,
                translation = match.translation
            )
        }
    }
}

private fun FeelingEntry.toFeeling(): Feeling {
    return Feeling(
        id = id,
        emoji = emoji,
        title = title,
        subtitle = subtitle,
        verses = verses,
        category = category
    )
}

