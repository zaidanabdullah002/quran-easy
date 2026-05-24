package com.zaidan.quraneasy.feature.tasbih.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TasbihDao {
    @Query("SELECT * FROM tasbih_table WHERE id= 1")
    fun getTasbihState(): Flow<TasbihEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTasbihState(tasbihEntity: TasbihEntity)

}
