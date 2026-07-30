package com.zaidan.quraneasy.feature.quran.data.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.zaidan.quraneasy.feature.quran.data.local.QuranDatabase
import com.zaidan.quraneasy.feature.quran.data.local.dao.AyahDao
import com.zaidan.quraneasy.feature.quran.data.local.dao.BookmarkDao
import com.zaidan.quraneasy.feature.quran.data.local.dao.SurahDao
import com.zaidan.quraneasy.feature.quran.data.local.dao.TranslationDao
import com.zaidan.quraneasy.feature.quran.data.local.dao.TranslationMetadataDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    private val defaultBookmarkSurahNumbers = listOf(
        67, // Al-Mulk
        18, // Al-Kahf
        64, // At-Taghabun
    )

    private val migration1To2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `bookmarks` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `type` TEXT NOT NULL,
                    `surahNumber` INTEGER,
                    `juzNumber` INTEGER,
                    `createdAt` INTEGER NOT NULL
                )
                """.trimIndent()
            )
            database.execSQL(
                "CREATE UNIQUE INDEX IF NOT EXISTS `index_bookmarks_type_surahNumber` ON `bookmarks` (`type`, `surahNumber`)"
            )
            database.execSQL(
                "CREATE UNIQUE INDEX IF NOT EXISTS `index_bookmarks_type_juzNumber` ON `bookmarks` (`type`, `juzNumber`)"
            )
        }
    }

    private val migration2To3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `translations` (
                    `surahNumber` INTEGER NOT NULL,
                    `numberInSurah` INTEGER NOT NULL,
                    `juzNumber` INTEGER NOT NULL,
                    `pageNumber` INTEGER NOT NULL,
                    `translationId` TEXT NOT NULL,
                    `languageCode` TEXT NOT NULL,
                    `text` TEXT NOT NULL,
                    PRIMARY KEY(`surahNumber`, `numberInSurah`, `translationId`)
                )
                """.trimIndent()
            )
            database.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_translations_juzNumber` ON `translations` (`juzNumber`)"
            )
            database.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_translations_pageNumber` ON `translations` (`pageNumber`)"
            )
            database.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `translation_metadata` (
                    `translationId` TEXT NOT NULL,
                    `languageCode` TEXT NOT NULL,
                    `displayName` TEXT NOT NULL,
                    `author` TEXT NOT NULL,
                    `downloadState` TEXT NOT NULL,
                    PRIMARY KEY(`translationId`)
                )
                """.trimIndent()
            )
        }
    }

    private val migration3To4 = object : Migration(3, 4) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `downloaded_juz` (
                    `juzNumber` INTEGER NOT NULL,
                    PRIMARY KEY(`juzNumber`)
                )
                """.trimIndent()
            )
        }
    }

    private val migration4To5 = object : Migration(4, 5) {
        override fun migrate(database: SupportSQLiteDatabase) {
            insertDefaultBookmarks(database)
        }
    }

    private val seedDefaultBookmarksCallback = object : RoomDatabase.Callback() {
        override fun onCreate(database: SupportSQLiteDatabase) {
            super.onCreate(database)
            insertDefaultBookmarks(database)
        }
    }

    private fun insertDefaultBookmarks(database: SupportSQLiteDatabase) {
        defaultBookmarkSurahNumbers.forEachIndexed { index, surahNumber ->
            database.execSQL(
                """
                INSERT OR IGNORE INTO `bookmarks`
                    (`type`, `surahNumber`, `juzNumber`, `createdAt`)
                VALUES ('SURAH', ?, NULL, ?)
                """.trimIndent(),
                arrayOf(
                    surahNumber,
                    (defaultBookmarkSurahNumbers.size - index).toLong(),
                )
            )
        }
    }

    @Provides
    @Singleton
    fun provideQuranDatabase(@ApplicationContext context: Context): QuranDatabase {
        return Room.databaseBuilder(
            context,
            QuranDatabase::class.java,
            "quran_db"
        )
            .addMigrations(
                migration1To2,
                migration2To3,
                migration3To4,
                migration4To5,
            )
            .addCallback(seedDefaultBookmarksCallback)
            .build()
    }

    @Provides
    @Singleton
    fun provideAyahDao(database: QuranDatabase): AyahDao {
        return database.ayahDao
    }

    @Provides
    @Singleton
    fun provideBookmarkDao(database: QuranDatabase): BookmarkDao {
        return database.bookmarkDao
    }

    @Provides
    @Singleton
    fun provideSurahDao(database: QuranDatabase): SurahDao {
        return database.surahDao
    }

    @Provides
    @Singleton
    fun provideTranslationDao(database: QuranDatabase): TranslationDao {
        return database.translationDao
    }

    @Provides
    @Singleton
    fun provideTranslationMetadataDao(database: QuranDatabase): TranslationMetadataDao {
        return database.translationMetadataDao
    }
}
