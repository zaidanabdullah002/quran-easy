package com.zaidan.quraneasy.feature.feeling.data

import com.zaidan.quraneasy.feature.feeling.presentation.Feeling
import com.zaidan.quraneasy.feature.feeling.presentation.FeelingVerseUiModel

interface FeelingRepository {
    suspend fun getHomeFeelings(): List<Feeling>
    suspend fun getFeeling(feelingId: String): Feeling
    suspend fun getFeelingVerses(feelingId: String): List<FeelingVerseUiModel>
}
