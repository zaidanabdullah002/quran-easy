package com.zaidan.quraneasy.feature.prayer.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PrayerEntity::class], version = 1)
abstract class PrayerDataBase : RoomDatabase(){
    abstract fun prayerDao(): PrayerDao
}