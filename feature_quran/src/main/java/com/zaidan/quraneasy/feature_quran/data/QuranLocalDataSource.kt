package com.zaidan.quraneasy.feature_quran.data

import com.zaidan.quraneasy.feature_quran.model.Surah
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class QuranLocalDataSource @Inject constructor(
    private val surahDao: SurahDao
) {
    fun observeSurahs(): Flow<List<Surah>> = surahDao.observeSurahs()
}

