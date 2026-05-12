package com.zaidan.quraneasy.feature_quran.data

import com.zaidan.quraneasy.feature_quran.model.Surah
import kotlinx.coroutines.flow.Flow

interface QuranRepository {
    fun observeSurahs(): Flow<List<Surah>>
}

