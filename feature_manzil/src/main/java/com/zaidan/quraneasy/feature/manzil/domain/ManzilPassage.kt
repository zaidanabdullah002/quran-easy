package com.zaidan.quraneasy.feature.manzil.domain

data class ManzilPassage(
    val surahNumber: Int,
    val surahName: String,
    val startAyah: Int,
    val endAyah: Int,
) {
    val title: String
        get() = when {
            surahNumber == 2 && startAyah == 1 -> "Opening verses"
            surahNumber == 2 && startAyah == 163 -> "Al-Baqarah 2:163"
            surahNumber == 2 && startAyah == 255 -> "Ayat al-Kursi"
            surahNumber == 2 && startAyah == 284 -> "Closing verses"
            else -> surahName
        }

    val verseRange: String
        get() = if (startAyah == endAyah) {
            "$surahNumber:$startAyah"
        } else {
            "$surahNumber:$startAyah–$endAyah"
        }
}

val manzilPassages = listOf(
    ManzilPassage(1, "Al-Fatihah", 1, 7),
    ManzilPassage(2, "Al-Baqarah", 1, 5),
    ManzilPassage(2, "Al-Baqarah", 163, 163),
    ManzilPassage(2, "Al-Baqarah", 255, 257),
    ManzilPassage(2, "Al-Baqarah", 284, 286),
    ManzilPassage(3, "Aal-e-Imran", 18, 18),
    ManzilPassage(3, "Aal-e-Imran", 26, 27),
    ManzilPassage(7, "Al-A'raf", 54, 56),
    ManzilPassage(17, "Al-Isra", 110, 111),
    ManzilPassage(23, "Al-Mu'minun", 115, 118),
    ManzilPassage(37, "As-Saffat", 1, 11),
    ManzilPassage(55, "Ar-Rahman", 33, 40),
    ManzilPassage(59, "Al-Hashr", 21, 24),
    ManzilPassage(72, "Al-Jinn", 1, 4),
    ManzilPassage(109, "Al-Kafirun", 1, 6),
    ManzilPassage(112, "Al-Ikhlas", 1, 4),
    ManzilPassage(113, "Al-Falaq", 1, 5),
    ManzilPassage(114, "An-Nas", 1, 6),
)
