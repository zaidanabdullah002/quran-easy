package com.zaidan.quraneasy.feature_quran.data

import com.zaidan.quraneasy.feature_quran.model.Surah
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class QuranRepositoryImpl @Inject constructor(
    private val localDataSource: QuranLocalDataSource
) : QuranRepository {
    override fun observeSurahs(): Flow<List<Surah>> = localDataSource.observeSurahs()
}

