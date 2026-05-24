package com.zaidan.quraneasy.feature.tasbih.domain.usecase

import com.zaidan.quraneasy.feature.tasbih.domain.repository.TasbihRepository
import javax.inject.Inject

class UpdateTasbihTargetUseCase @Inject constructor(
    private val repository: TasbihRepository
) {
    suspend operator fun invoke(target: Int) = repository.saveTarget(target)
}
