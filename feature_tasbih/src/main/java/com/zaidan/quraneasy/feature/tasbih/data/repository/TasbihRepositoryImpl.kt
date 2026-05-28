package com.zaidan.quraneasy.feature.tasbih.data.repository

import com.zaidan.quraneasy.feature.tasbih.data.local.TasbihDao
import com.zaidan.quraneasy.feature.tasbih.data.local.TasbihEntity
import com.zaidan.quraneasy.feature.tasbih.domain.TasbihState
import com.zaidan.quraneasy.feature.tasbih.domain.repository.TasbihRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TasbihRepositoryImpl @Inject constructor(
    private val tasbihDao: TasbihDao
) : TasbihRepository {

    override fun observeTasbihState(): Flow<TasbihState> {
        return tasbihDao.getTasbihState().map { entity ->
            entity?.toDomainState() ?: TasbihState()
        }
    }

    override suspend fun saveCount(count: Int) {
        val current = currentEntity()
        tasbihDao.upsertTasbihState(current.copy(count = count))
    }

    override suspend fun saveTarget(target: Int) {
        val current = currentEntity()
        tasbihDao.upsertTasbihState(current.copy(target = target))
    }

    override suspend fun saveTasbihText(tasbihText: String) {
        val current = currentEntity()
        tasbihDao.upsertTasbihState(current.copy(tasbihText = tasbihText))
    }

    override suspend fun reset() {
        val current = currentEntity()
        tasbihDao.upsertTasbihState(current.copy(count = 0))
    }

    private suspend fun currentEntity(): TasbihEntity {
        return tasbihDao.getTasbihState().first() ?: TasbihEntity(
            id = 1,
            count = 0,
            target = 33,
            tasbihText = "subhan allah"
        )
    }

    private fun TasbihEntity.toDomainState(): TasbihState {
        return TasbihState(
            count = count,
            target = target,
            tasbihText = tasbihText
        )
    }
}
