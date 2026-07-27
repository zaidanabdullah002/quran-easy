package com.zaidan.quraneasy.feature.prayer.data.repository

import com.zaidan.quraneasy.feature.prayer.data.local.PrayerDao
import com.zaidan.quraneasy.feature.prayer.data.local.entity.PrayerCompletionEntity
import com.zaidan.quraneasy.feature.prayer.domain.Prayer
import com.zaidan.quraneasy.feature.prayer.domain.PrayerState
import com.zaidan.quraneasy.feature.prayer.domain.PrayerSchedule
import com.zaidan.quraneasy.feature.prayer.data.local.entity.PrayerScheduleEntity
import com.zaidan.quraneasy.feature.prayer.domain.repository.PrayerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrayerRepositoryImpl @Inject constructor(
    private val prayerDao: PrayerDao
) : PrayerRepository {

    override fun observePrayerState(
        date: String,
        locationKey: String
    ): Flow<PrayerState> {
        return prayerDao.observeSchedules(date, locationKey)
            .combine(prayerDao.getCompletionsFlow(date)) { schedules, completions ->
                val completionByName = completions.associateBy { it.name }
                PrayerState(
                    prayers = schedules.map { schedule ->
                        Prayer(
                            name = schedule.name,
                            time = schedule.time,
                            completed = completionByName[schedule.name]?.completed == true
                        )
                    },
                    locationLabel = schedules.firstOrNull()?.locationLabel
                )
            }
    }

    override suspend fun getCachedLocationKey(date: String): String? {
        return prayerDao.getCachedLocationKey(date)
    }

    override suspend fun getCachedLocationLabel(date: String, locationKey: String): String? {
        return prayerDao.getCachedLocationLabel(date, locationKey)
    }

    override suspend fun hasSchedule(date: String, locationKey: String): Boolean =
        prayerDao.getSchedules(date, locationKey).isNotEmpty()

    override suspend fun saveSchedule(schedule: PrayerSchedule, locationLabel: String) {
        prayerDao.replaceSchedule(
            date = schedule.date,
            locationKey = schedule.locationKey,
            schedules = schedule.prayers.map { prayer ->
                PrayerScheduleEntity(
                    date = schedule.date,
                    locationKey = schedule.locationKey,
                    locationLabel = locationLabel,
                    name = prayer.name,
                    time = prayer.time
                )
            }
        )
    }

    override suspend fun updateLocationLabel(
        date: String,
        locationKey: String,
        locationLabel: String
    ) {
        prayerDao.updateLocationLabel(date, locationKey, locationLabel)
    }

    override suspend fun togglePrayerCompletion(date: String, prayerName: String) {
        val entity = prayerDao.getCompletion(date = date, name = prayerName)
        prayerDao.upsertCompletion(
            entity?.copy(
                date = date,
                name = prayerName,
                completed = entity.completed.not()
            )
                ?: PrayerCompletionEntity(
                date = date,name = prayerName, completed = true
            )
        )
    }
}
