package com.zaidan.quraneasy.feature.quran.domain.usecase

import javax.inject.Inject

data class QuranUseCasesData @Inject constructor(
    val observeSurahs: ObserveSurahsUseCase,
    val observeSurah: ObserveSurahUseCase,
    val observeJuz: ObserveJuzUseCase,
    val observeBookmarks: ObserveBookmarksUseCase,
    val isSurahBookmarked: IsSurahBookmarkedUseCase,
    val isJuzBookmarked: IsJuzBookmarkedUseCase,
    val observePage: ObservePageUseCase,
    val ensureSurahListDownloaded: EnsureSurahListDownloadedUseCase,
    val ensureSurahDownloaded: EnsureSurahDownloadedUseCase,
    val ensureJuzDownloaded: EnsureJuzDownloadedUseCase,
    val addSurahBookmark: AddSurahBookmarkUseCase,
    val addJuzBookmark: AddJuzBookmarkUseCase,
    val removeSurahBookmark: RemoveSurahBookmarkUseCase,
    val removeJuzBookmark: RemoveJuzBookmarkUseCase,
)
