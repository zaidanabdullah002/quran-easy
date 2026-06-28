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
    suspend fun getTranslation(
        surahNumber: Int,
        ayahNumber: Int,
        translationId: String = DefaultTranslationId
    ): String? {
        val cached = translationDao.getTranslation(
            surahNumber = surahNumber,
            ayahNumber = ayahNumber,
            translationId = translationId
        )
        if (cached != null) return cached.text

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

    companion object {
        const val DefaultTranslationId = "en.asad"
    }
}
