package com.zaidan.quraneasy.feature.quran.presentation

object QuranRoutes {
    const val list = "quran"
    const val reader = "quran_reader/{surahNumber}"

    fun reader(surahNumber: Int): String = "quran_reader/$surahNumber"
}
