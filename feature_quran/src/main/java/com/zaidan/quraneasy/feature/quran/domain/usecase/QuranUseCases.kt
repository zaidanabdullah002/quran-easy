package com.zaidan.quraneasy.feature.quran.domain.usecase

import com.zaidan.quraneasy.feature.quran.domain.repository.QuranRepository
import javax.inject.Inject

class ObserveSurahsUseCase @Inject constructor(
    private val repository: QuranRepository,
) {
    operator fun invoke() =
        repository.observeSurahs()
}

class ObserveSurahUseCase @Inject constructor(
    private val repository: QuranRepository,
) {
    operator fun invoke(
        surahNumber: Int,
    ) = repository.observeSurah(surahNumber)
}

class ObserveJuzUseCase @Inject constructor(
    private val repository: QuranRepository,
) {
    operator fun invoke(
        juzNumber: Int,
    ) = repository.observeJuz(juzNumber)
}

class ObservePageUseCase @Inject constructor(
    private val repository: QuranRepository,
) {
    operator fun invoke(
        pageNumber: Int,
    ) = repository.observePage(pageNumber)
}

class EnsureSurahListDownloadedUseCase @Inject constructor(
    private val repository: QuranRepository,
){
    suspend operator fun invoke() =
        repository.ensureSurahListDownloaded()
}

class EnsureSurahDownloadedUseCase @Inject constructor(
    private val repository: QuranRepository,
) {
    suspend operator fun invoke(
        surahNumber: Int,
    ) = repository.ensureSurahDownloaded(surahNumber)
}

class EnsureJuzDownloadedUseCase @Inject constructor(
    private val repository: QuranRepository,
){
    suspend operator fun invoke(
        juzNumber: Int,
    ) = repository.ensureJuzDownloaded(juzNumber)
}

