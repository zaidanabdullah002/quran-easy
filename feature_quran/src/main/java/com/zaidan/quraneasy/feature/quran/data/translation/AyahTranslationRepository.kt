package com.zaidan.quraneasy.feature.quran.data.translation

import com.zaidan.quraneasy.feature.quran.data.local.dao.TranslationDao
import com.zaidan.quraneasy.feature.quran.data.local.entity.TranslationEntity
import com.zaidan.quraneasy.feature.quran.data.remote.QuranApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AyahTranslationRepository @Inject constructor(
    private val translationDao: TranslationDao,
    private val quranApiService: QuranApiService
) {
    suspend fun getCachedTranslation(
        surahNumber: Int,
        ayahNumber: Int,
        translationId: String = DefaultTranslationId
    ): String? = translationDao.getTranslation(
        surahNumber = surahNumber,
        ayahNumber = ayahNumber,
        translationId = translationId
    )?.text

    suspend fun getTranslation(
        surahNumber: Int,
        ayahNumber: Int,
        translationId: String = DefaultTranslationId
    ): String? {
        val cached = getCachedTranslation(
            surahNumber = surahNumber,
            ayahNumber = ayahNumber,
            translationId = translationId
        )
        if (cached != null) return cached

        val response = quranApiService.getAyahTranslation(
            reference = "$surahNumber:$ayahNumber",
            edition = translationId
        ).data

        val entity = TranslationEntity(
            surahNumber = surahNumber,
            numberInSurah = response.numberInSurah,
            juzNumber = response.juz,
            pageNumber = response.page,
            translationId = response.edition.identifier,
            languageCode = response.edition.language,
            text = response.text
        )
        translationDao.upsertTranslation(entity)
        return entity.text
    }

    suspend fun getSurahTranslations(
        surahNumber: Int,
        ayahNumbers: Set<Int>,
        translationId: String = DefaultTranslationId,
    ): Map<Int, String> {
        val cached = ayahNumbers.associateWith { ayahNumber ->
            getCachedTranslation(surahNumber, ayahNumber, translationId)
        }
        if (cached.values.none { it == null }) {
            return cached.mapValues { (_, text) -> text.orEmpty() }
        }

        val byNumber = quranApiService
            .getSurahEdition(surahNumber, translationId)
            .data
            .ayahs
            .associateBy { it.numberInSurah }
        ayahNumbers.forEach { ayahNumber ->
            val ayah = byNumber[ayahNumber] ?: error("No English for $surahNumber:$ayahNumber")
            translationDao.upsertTranslation(
                TranslationEntity(
                    surahNumber = surahNumber,
                    numberInSurah = ayah.numberInSurah,
                    juzNumber = ayah.juz,
                    pageNumber = ayah.page,
                    translationId = translationId,
                    languageCode = "en",
                    text = ayah.text,
                )
            )
        }
        return ayahNumbers.associateWith { ayahNumber ->
            byNumber.getValue(ayahNumber).text
        }
    }

    companion object {
        const val DefaultTranslationId = "en.asad"
    }
}
