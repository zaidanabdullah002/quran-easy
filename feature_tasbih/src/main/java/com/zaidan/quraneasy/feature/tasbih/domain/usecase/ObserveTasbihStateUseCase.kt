package com.zaidan.quraneasy.feature.tasbih.domain.usecase

import com.zaidan.quraneasy.feature.tasbih.domain.repository.TasbihRepository
import com.zaidan.quraneasy.feature.tasbih.presentation.TasbihUiState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveTasbihStateUseCase @Inject constructor(
    private val repository: TasbihRepository
) {
    operator fun invoke(): Flow<TasbihUiState> = repository.observeTasbihState()
}
