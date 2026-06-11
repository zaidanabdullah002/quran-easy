package com.zaidan.quraneasy.feature.feeling.data

import com.zaidan.quraneasy.feature.feeling.presentation.Feeling
import com.zaidan.quraneasy.feature.feeling.presentation.FeelingDetailUiState

interface FeelingRepository {
    suspend fun getHomeFeelings(): List<Feeling>
    suspend fun getFeelingDetail(feelingId: String): FeelingDetailUiState
}

