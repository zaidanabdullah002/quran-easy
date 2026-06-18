package com.zaidan.quraneasy.feature.prayer.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.zaidan.quraneasy.feature.prayer.data.local.entity.PrayerCompletionEntity
import com.zaidan.quraneasy.feature.prayer.data.local.entity.PrayerScheduleEntity

@Database(
    entities = [
        PrayerScheduleEntity::class,
        PrayerCompletionEntity::class
    ],
    version = 3
)
abstract class PrayerDataBase : RoomDatabase(){
    abstract fun prayerDao(): PrayerDao
}
