package com.zaidan.quraneasy.feature.tasbih.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [TasbihEntity::class], version = 1)
abstract class TasbihDatabase : RoomDatabase(){
    abstract fun tasbihDao(): TasbihDao
}