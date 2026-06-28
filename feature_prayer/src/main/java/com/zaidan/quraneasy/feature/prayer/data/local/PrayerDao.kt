package com.zaidan.quraneasy.feature.prayer.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zaidan.quraneasy.feature.prayer.data.local.entity.PrayerCompletionEntity
import com.zaidan.quraneasy.feature.prayer.data.local.entity.PrayerScheduleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PrayerDao {

    @Query(
        """
        SELECT * FROM prayer_schedule
        WHERE date = :date AND locationKey = :locationKey
        ORDER BY CASE name
            WHEN 'Fajr' THEN 1
            WHEN 'Sunrise' THEN 2
            WHEN 'Dhuhr' THEN 3
            WHEN 'Asr' THEN 4
            WHEN 'Maghrib' THEN 5
            WHEN 'Isha' THEN 6
            ELSE 7
        END
        """
    )
    fun observeSchedules(
        date: String,
        locationKey: String
    ): Flow<List<PrayerScheduleEntity>>

    @Query("SELECT locationKey FROM prayer_schedule WHERE date = :date LIMIT 1")
    suspend fun getCachedLocationKey(date: String): String?

    @Query("SELECT locationLabel FROM prayer_schedule WHERE date = :date AND locationKey = :locationKey LIMIT 1")
    suspend fun getCachedLocationLabel(date: String, locationKey: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSchedules(
        schedules: List<PrayerScheduleEntity>
    )

    @Query(
        """
        SELECT * FROM prayer_schedule
        WHERE date = :date AND locationKey = :locationKey
        ORDER BY CASE name
            WHEN 'Fajr' THEN 1
            WHEN 'Sunrise' THEN 2
            WHEN 'Dhuhr' THEN 3
            WHEN 'Asr' THEN 4
            WHEN 'Maghrib' THEN 5
            WHEN 'Isha' THEN 6
            ELSE 7
        END
        """
    )
    suspend fun getSchedules(
        date: String,
        locationKey: String
    ): List<PrayerScheduleEntity>

    @Query("SELECT * FROM prayer_completion WHERE date = :date")
    suspend fun getCompletions(date: String): List<PrayerCompletionEntity>

    @Query("SELECT * FROM prayer_completion WHERE date = :date")
    fun getCompletionsFlow(date: String): Flow<List<PrayerCompletionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCompletion(
        completion: PrayerCompletionEntity
    )

    @Query("SELECT * FROM prayer_completion WHERE date = :date AND name = :name LIMIT 1")
    suspend fun getCompletion(
        date: String,
        name: String
    ): PrayerCompletionEntity?

    @Query("DELETE FROM prayer_schedule WHERE date != :date")
    suspend fun deleteSchedulesExcept(date: String)

    @Query("DELETE FROM prayer_completion WHERE date != :date")
    suspend fun deleteCompletionsExcept(date: String)
}
