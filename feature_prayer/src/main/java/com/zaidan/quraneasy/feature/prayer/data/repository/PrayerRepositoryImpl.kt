package com.zaidan.quraneasy.feature.prayer.data.repository

import com.zaidan.quraneasy.feature.prayer.data.local.PrayerDao
import com.zaidan.quraneasy.feature.prayer.data.local.PrayerEntity
import com.zaidan.quraneasy.feature.prayer.domain.Prayer
import com.zaidan.quraneasy.feature.prayer.domain.PrayerState
import com.zaidan.quraneasy.feature.prayer.domain.repository.PrayerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrayerRepositoryImpl @Inject constructor(
    private val prayerDao : PrayerDao
)  : PrayerRepository {
    override fun observePrayerStatus(): Flow<PrayerState> {
        return  prayerDao.observePrayers().map { entities ->
            PrayerState(
                prayers = entities.map {
                    it.toDomain()
                }
            )
        }

    }

    override suspend fun updatePrayerCompletion(prayerName: String) {
        prayerDao.togglePrayerCompletion(prayerName)
    }

    override suspend fun initPrayerData() {
        val prayerList = listOf(
            PrayerEntity("Fajr", "04:45", false),
            PrayerEntity("Dhuhr", "12:00", false),
            PrayerEntity("Asr", "16:00", false),
            PrayerEntity("Maghrib", "18:00", false),
            PrayerEntity("Isha", "19:00", false)
        )
        prayerDao.upsertAll(prayerList)
    }

    fun PrayerEntity.toDomain() = Prayer(name, time, completed)

}
