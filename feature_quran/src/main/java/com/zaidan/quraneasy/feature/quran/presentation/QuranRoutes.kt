package com.zaidan.quraneasy.feature.quran.presentation

object QuranRoutes {
    const val list = "quran"
    const val reader = "quran_reader/{readerType}/{itemNumber}"

    fun reader(readerType: Int,itemNumber: Int): String = "quran_reader/$readerType/$itemNumber"

}
