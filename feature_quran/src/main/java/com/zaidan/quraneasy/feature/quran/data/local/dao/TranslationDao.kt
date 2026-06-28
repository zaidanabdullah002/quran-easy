package com.zaidan.quraneasy.feature.quran.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zaidan.quraneasy.feature.quran.data.local.entity.TranslationEntity

@Dao
interface TranslationDao {
    @Query(
        """
        SELECT * FROM translations
        WHERE surahNumber = :surahNumber
            AND numberInSurah = :ayahNumber
            AND translationId = :translationId
        LIMIT 1
        """
    )
    suspend fun getTranslation(
        surahNumber: Int,
        ayahNumber: Int,
        translationId: String
    ): TranslationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTranslation(translation: TranslationEntity)
}
