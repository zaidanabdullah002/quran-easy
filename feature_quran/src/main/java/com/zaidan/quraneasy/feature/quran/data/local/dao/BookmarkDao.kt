package com.zaidan.quraneasy.feature.quran.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zaidan.quraneasy.feature.quran.data.local.entity.BookmarkEntity
import com.zaidan.quraneasy.feature.quran.data.local.entity.BookmarkType
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {

    @Query("SELECT * FROM bookmarks ORDER BY createdAt DESC")
    fun observeBookmarks(): Flow<List<BookmarkEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE type = :type AND surahNumber = :surahNumber")
    suspend fun deleteSurahBookmark(type: BookmarkType = BookmarkType.SURAH, surahNumber: Int)

    @Query("DELETE FROM bookmarks WHERE type = :type AND juzNumber = :juzNumber")
    suspend fun deleteJuzBookmark(type: BookmarkType = BookmarkType.JUZ, juzNumber: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE type = :type AND surahNumber = :surahNumber)")
    fun isSurahBookmarked(type: BookmarkType = BookmarkType.SURAH, surahNumber: Int): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE type = :type AND juzNumber = :juzNumber)")
    fun isJuzBookmarked(type: BookmarkType = BookmarkType.JUZ, juzNumber: Int): Flow<Boolean>
}
