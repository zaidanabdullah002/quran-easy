package com.zaidan.quraneasy.feature.tasbih.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasbih_table")
data class TasbihEntity(
    @PrimaryKey val id: Int = 1, //fixed value
    val count: Int,
    val target: Int,
    val tasbihText: String,
    val lastUpdated: Long = System.currentTimeMillis()
)