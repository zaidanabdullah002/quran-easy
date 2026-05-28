package com.zaidan.quraneasy.feature.tasbih.domain.repository

import com.zaidan.quraneasy.feature.tasbih.domain.TasbihState
import kotlinx.coroutines.flow.Flow

interface TasbihRepository {
    fun observeTasbihState(): Flow<TasbihState>
    suspend fun saveCount(count: Int)
    suspend fun saveTarget(target: Int)
    suspend fun saveTasbihText(tasbihText: String)
    suspend fun reset()
}
