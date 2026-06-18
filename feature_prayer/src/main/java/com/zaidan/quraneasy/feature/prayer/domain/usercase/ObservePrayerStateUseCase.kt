package com.zaidan.quraneasy.feature.prayer.domain.usercase

import com.zaidan.quraneasy.feature.prayer.domain.repository.PrayerRepository
import com.zaidan.quraneasy.feature.prayer.domain.PrayerState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservePrayerStateUseCase @Inject constructor(
    private val repository: PrayerRepository
) {
    operator fun invoke(date: String, locationKey: String): Flow<PrayerState> {
        return repository.observePrayerState(date, locationKey)
    }
}
