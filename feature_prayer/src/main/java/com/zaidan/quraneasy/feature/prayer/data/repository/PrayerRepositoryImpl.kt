package com.zaidan.quraneasy.feature.prayer.data.repository

import android.util.Log
import com.zaidan.quraneasy.feature.prayer.data.local.PrayerDao
import com.zaidan.quraneasy.feature.prayer.data.local.entity.PrayerCompletionEntity
import com.zaidan.quraneasy.feature.prayer.data.remote.PrayerTimingsRemoteDataSource
import com.zaidan.quraneasy.feature.prayer.domain.Prayer
import com.zaidan.quraneasy.feature.prayer.domain.PrayerDataResult
import com.zaidan.quraneasy.feature.prayer.domain.PrayerState
import com.zaidan.quraneasy.feature.prayer.domain.repository.PrayerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrayerRepositoryImpl @Inject constructor(
    private val prayerDao: PrayerDao,
    private val remoteDataSource: PrayerTimingsRemoteDataSource
) : PrayerRepository {

    companion object{
        private const val TAG = "PrayerRepositoryImpl"
    }

    override fun observePrayerState(
        date: String,
        locationKey: String
    ): Flow<PrayerState> {
        Log.i(TAG, "observePrayerState(): date=$date locationKey=$locationKey")
        return prayerDao.observeSchedules(date, locationKey)
            .combine(prayerDao.getCompletionsFlow(date)) { schedules, completions ->
                Log.d(
                    TAG,
                    "observePrayerState(): schedules=${schedules.size} completions=${completions.size} date=$date locationKey=$locationKey"
                )
                val completionByName = completions.associateBy { it.name }
                PrayerState(
                    prayers = schedules.map { schedule ->
                        Prayer(
                            name = schedule.name,
                            time = schedule.time,
                            completed = completionByName[schedule.name]?.completed == true
                        )
                    }
                )
            }
    }

    override suspend fun getCachedLocationKey(date: String): String? {
        val locationKey = prayerDao.getCachedLocationKey(date)
        Log.i(TAG, "getCachedLocationKey(): date=$date locationKey=$locationKey")
        return locationKey
    }

    override suspend fun getCachedLocationLabel(date: String, locationKey: String): String? {
        val locationLabel = prayerDao.getCachedLocationLabel(date, locationKey)
        Log.i(TAG, "getCachedLocationLabel(): date=$date locationKey=$locationKey locationLabel=$locationLabel")
        return locationLabel
    }

    override suspend fun refreshPrayerSchedule(
        date: String,
        latitude: Double,
        longitude: Double,
        locationKey: String,
        locationLabel: String
    ): PrayerDataResult {
        val cachedSchedules = prayerDao.getSchedules(date, locationKey)
        Log.i(
            TAG,
            "refreshPrayerSchedule(): date=$date lat=$latitude long=$longitude locationKey=$locationKey locationLabel=$locationLabel cachedSchedules=${cachedSchedules.size}"
        )
        return try {
            val remoteSchedules = remoteDataSource.fetchPrayerTimings(
                date = date,
                latitude = latitude,
                longitude = longitude,
                locationKey = locationKey,
                locationLabel = locationLabel
            )
            Log.i(TAG, "refreshPrayerSchedule(): fetched remoteSchedules=${remoteSchedules.size}")
            prayerDao.upsertSchedules(remoteSchedules)
            prayerDao.deleteSchedulesExcept(date)
            prayerDao.deleteCompletionsExcept(date)
            Log.d(TAG, "refreshPrayerSchedule(): persisted schedules and cleaned old rows")
            PrayerDataResult.Fresh
        } catch (error: Exception) {
            Log.e(TAG, "refreshPrayerSchedule(): failed for $date/$locationKey", error)
            if (cachedSchedules.isNotEmpty()) {
                PrayerDataResult.Cached
            } else {
                PrayerDataResult.Unavailable
            }
        }
    }

    override suspend fun togglePrayerCompletion(date: String, prayerName: String) {
        Log.i(TAG,"togglePrayerCompletion(): prayerName=$prayerName, date=$date")
        val entity = prayerDao.getCompletion(date = date, name = prayerName)
        Log.d(TAG,"togglePrayerCompletion(): entity=$entity")
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
