package com.zaidan.quraneasy.feature_quran.domain

import com.zaidan.quraneasy.feature_quran.data.QuranRepository
import com.zaidan.quraneasy.feature_quran.model.Surah
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSurahsUseCase @Inject constructor(
    private val repository: QuranRepository
) {
    operator fun invoke(): Flow<List<Surah>> = repository.observeSurahs()
}

