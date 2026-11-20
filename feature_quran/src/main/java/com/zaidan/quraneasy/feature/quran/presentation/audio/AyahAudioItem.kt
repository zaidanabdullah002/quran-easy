package com.zaidan.quraneasy.feature.quran.presentation.audio

/** Stable Quran coordinates independent of any reader or detail screen model. */
data class AyahAudioItem(val surahNumber: Int, val ayahNumber: Int) {
    init {
        require(surahNumber in 1..114)
        require(ayahNumber > 0)
    }
}

/** Audio source can be changed without changing playback or screen state. */
interface AyahAudioSource {
    fun urlFor(ayah: AyahAudioItem): String
}

object AlafasyAudioSource : AyahAudioSource {
    override fun urlFor(ayah: AyahAudioItem): String =
        alafasyAyahAudioUrl(ayah.surahNumber, ayah.ayahNumber)
}
