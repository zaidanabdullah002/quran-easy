package com.zaidan.quraneasy.feature.prayer.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface PrayerDao {

    @Query("SELECT * FROM prayer_entity")
    fun observePrayers() : Flow<List<PrayerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPrayerState(prayerEntity: PrayerEntity)

    @Query("UPDATE prayer_entity SET completed = NOT completed WHERE name = :prayerName")
    suspend fun togglePrayerCompletion(prayerName: String)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(prayerEntities: List<PrayerEntity>)
}