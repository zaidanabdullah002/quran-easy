package com.zaidan.quraneasy.feature.tasbih.domain.usecase

import com.zaidan.quraneasy.feature.tasbih.domain.repository.TasbihRepository
import javax.inject.Inject

class UpdateTasbihCountUseCase @Inject constructor(
    private val repository: TasbihRepository
) {
    suspend operator fun invoke(count: Int) = repository.saveCount(count)
}
