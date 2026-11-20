package com.zaidan.quraneasy.feature.manzil.presentation

import com.zaidan.quraneasy.feature.manzil.domain.manzilPassages
import com.zaidan.quraneasy.feature.quran.presentation.audio.AyahAudioItem
import org.junit.Assert.assertEquals
import org.junit.Test

class ManzilAudioPlaylistTest {
    private val passages = manzilPassages.map { passage ->
        ManzilPassageUi(passage.title, passage.surahNumber, passage.surahName,
            passage.startAyah, passage.endAyah,
            (passage.startAyah..passage.endAyah).map { ManzilAyahUi(it, "Arabic", "") })
    }

    @Test fun allPassagesHaveCorrectCoverageAndGaps() {
        val queue = passages.toAudioPlaylist()
        assertEquals(18, passages.size)
        assertEquals(79, queue.size)
        assertEquals(AyahAudioItem(1, 1), queue.first())
        assertEquals(AyahAudioItem(114, 6), queue.last())
        assertEquals(listOf(AyahAudioItem(2, 5), AyahAudioItem(2, 163), AyahAudioItem(2, 255)),
            queue.subList(11, 14))
        assertEquals(queue.size, queue.distinct().size)
    }

    @Test fun individualPassageDoesNotPlayOtherRanges() {
        assertEquals(listOf(AyahAudioItem(2, 255), AyahAudioItem(2, 256), AyahAudioItem(2, 257)),
            listOf(passages[3]).toAudioPlaylist())
    }

    @Test fun emptyContentHasNoAudioQueue() {
        assertEquals(emptyList<AyahAudioItem>(), emptyList<ManzilPassageUi>().toAudioPlaylist())
    }
}
