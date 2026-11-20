package com.zaidan.quraneasy.feature.quran.presentation.audio

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class QuranAyahAudioPlayerTest {
    @Test
    fun `formats Alafasy ayah URL with zero padded surah and ayah`() {
        assertEquals(
            "https://everyayah.com/data/Alafasy_128kbps/018001.mp3",
            alafasyAyahAudioUrl(surahNumber = 18, ayahNumber = 1)
        )
    }

    @Test
    fun `rejects invalid Quran coordinates`() {
        assertThrows(IllegalArgumentException::class.java) {
            alafasyAyahAudioUrl(surahNumber = 0, ayahNumber = 1)
        }
        assertThrows(IllegalArgumentException::class.java) {
            alafasyAyahAudioUrl(surahNumber = 18, ayahNumber = 0)
        }
    }

    @Test
    fun `audio source resolves ayahs across surah boundaries`() {
        assertEquals(
            "https://everyayah.com/data/Alafasy_128kbps/001007.mp3",
            AlafasyAudioSource.urlFor(AyahAudioItem(1, 7))
        )
        assertEquals(
            "https://everyayah.com/data/Alafasy_128kbps/002001.mp3",
            AlafasyAudioSource.urlFor(AyahAudioItem(2, 1))
        )
    }
}
