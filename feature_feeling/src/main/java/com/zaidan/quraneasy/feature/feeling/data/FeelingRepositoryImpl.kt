package com.zaidan.quraneasy.feature.feeling.data

import com.zaidan.quraneasy.feature.feeling.presentation.Feeling
import com.zaidan.quraneasy.feature.feeling.presentation.FeelingCategory
import com.zaidan.quraneasy.feature.feeling.presentation.FeelingEntry
import com.zaidan.quraneasy.feature.feeling.presentation.FeelingVerseUiModel
import com.zaidan.quraneasy.feature.feeling.presentation.VerseRef
import com.zaidan.quraneasy.feature.quran.data.translation.AyahTranslationRepository
import com.zaidan.quraneasy.feature.quran.domain.usecase.QuranUseCasesData
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeelingRepositoryImpl @Inject constructor(
    private val localDataSource: FeelingLocalDataSource,
    private val quranUseCases: QuranUseCasesData,
    private val ayahTranslationRepository: AyahTranslationRepository
) : FeelingRepository {

    override suspend fun getHomeFeelings(): List<Feeling> {
        return localDataSource.loadCatalog()
            .filter { it.category == FeelingCategory.Feeling || it.category == FeelingCategory.QuranDua }
            .map { entry ->
                entry.toFeeling(
                    previewTranslation = loadPreviewTranslation(entry.verses.firstOrNull())
                )
            }
    }

    override suspend fun getFeeling(feelingId: String): Feeling =
        findFeelingEntry(feelingId).toFeeling()

    override suspend fun getFeelingVerses(feelingId: String): List<FeelingVerseUiModel> =
        loadVerses(findFeelingEntry(feelingId).verses)

    private suspend fun loadVerses(verseRefs: List<VerseRef>): List<FeelingVerseUiModel> {
        quranUseCases.ensureSurahListDownloaded()
        verseRefs
            .map { it.surah }
            .distinct()
            .forEach { surahNumber ->
                quranUseCases.ensureSurahDownloaded(surahNumber)
            }

        return verseRefs.mapNotNull { verseRef ->
            val ayahs = quranUseCases.observeSurah(verseRef.surah).first()
            val match = ayahs.firstOrNull { it.numberInSurah == verseRef.ayah } ?: return@mapNotNull null
            FeelingVerseUiModel(
                surahNumber = match.surahNumber,
                numberInSurah = match.numberInSurah,
                arabicText = match.arabicText,
                translation = ayahTranslationRepository.getTranslation(
                    surahNumber = verseRef.surah,
                    ayahNumber = verseRef.ayah
                )
            )
        }
    }

    private suspend fun loadPreviewTranslation(
        verseRef: VerseRef?
    ): String? {
        verseRef ?: return null
        return runCatching {
            ayahTranslationRepository.getTranslation(
                surahNumber = verseRef.surah,
                ayahNumber = verseRef.ayah
            )
        }.getOrNull()?.takeIf { it.isNotBlank() }
    }

    private fun findFeelingEntry(feelingId: String): FeelingEntry {
        val catalog = localDataSource.loadCatalog()
        return catalog.firstOrNull { it.id == feelingId } ?: catalog.first()
    }
}

private fun FeelingEntry.toFeeling(
    previewTranslation: String? = null
): Feeling {
    return Feeling(
        id = id,
        emoji = emoji,
        title = title,
        subtitle = subtitle,
        verses = verses,
        previewTranslation = previewTranslation,
        accent = accent,
        artworkKey = artworkKey,
        category = category
    )
}
