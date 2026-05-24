package com.zaidan.quraneasy.feature.tasbih.domain.repository

import com.zaidan.quraneasy.feature.tasbih.presentation.TasbihUiState
import kotlinx.coroutines.flow.Flow

interface TasbihRepository {
    fun observeTasbihState(): Flow<TasbihUiState>
    suspend fun saveCount(count: Int)
    suspend fun saveTarget(target: Int)
    suspend fun saveTasbihText(tasbihText: String)
    suspend fun reset()
}
