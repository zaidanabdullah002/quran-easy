package com.zaidan.quraneasy.feature.quran.domain.usecase

import javax.inject.Inject

data class QuranUseCasesData @Inject constructor(
    val observeSurahs: ObserveSurahsUseCase,
    val observeSurah: ObserveSurahUseCase,
    val observeJuz: ObserveJuzUseCase,
    val observePage: ObservePageUseCase,
    val ensureSurahListDownloaded: EnsureSurahListDownloadedUseCase,
    val ensureSurahDownloaded: EnsureSurahDownloadedUseCase,
    val ensureJuzDownloaded: EnsureJuzDownloadedUseCase,
    )