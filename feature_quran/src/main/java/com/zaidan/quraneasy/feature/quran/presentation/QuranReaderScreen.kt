package com.zaidan.quraneasy.feature.quran.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaidan.quraneasy.feature.quran.data.model.AyahUiModel

private val ReaderBackground = Color(0xFFF3F3F3)
private val ReaderTopBar = Color(0xFF2B2B2B)
private val FooterBorder = Color(0xFFD8D8D8)

@Preview(showBackground = true)
@Composable
private fun QuranReaderScreenPreview() {
    QuranReaderScreen(
        onBackClick = {},
        surahNumber = 1
    )
}

@Composable
fun QuranReaderScreen(
    onBackClick: () -> Unit,
    surahNumber: Int
) {
    val surah = if (surahNumber == 1) {
        readerSampleSurah()
    } else {
        readerSampleSurah().copy(
            surahNameEnglish = "Al-Baqarah",
            surahNameArabic = "البقرة",
            versesLabel = "286 verses"
        )
    }

    val ayahs = readerSampleAyahs()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ReaderBackground)
    ) {
        QuranReaderTopBar(
            surah = surah,
            onBackClick = onBackClick
        )

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(ayahs) { ayah ->
                QuranAyahCard(ayah = ayah)
            }
        }

        QuranReaderFooter(
            footerText = "Surah ${surahNumber} • Meccan"
        )
    }
}

@Composable
private fun QuranReaderTopBar(
    surah: AyahUiModel,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ReaderTopBar)
            .padding(horizontal = 14.dp, vertical = 16.dp)
    ) {
        RowCenteredTopBar(
            onBackClick = onBackClick,
            surah = surah
        )
    }
}

@Composable
private fun RowCenteredTopBar(
    onBackClick: () -> Unit,
    surah: AyahUiModel
) {
    androidx.compose.foundation.layout.Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.Outlined.KeyboardArrowLeft,
                contentDescription = "Back",
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.padding(start = 4.dp))

        Icon(
            imageVector = Icons.Outlined.Book,
            contentDescription = null,
            tint = Color.White
        )

        Spacer(modifier = Modifier.padding(start = 8.dp))

        Column(modifier = Modifier.fillMaxWidth(0.62f)) {
            Text(
                text = surah.surahNameEnglish,
                color = Color.White,
                fontSize = 23.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "${surah.surahNameArabic} • ${surah.versesLabel}",
                color = Color.White.copy(alpha = 0.78f),
                fontSize = 16.sp
            )
        }

        Text(
            text = surah.surahNameArabic,
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            fontSize = 30.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun QuranAyahCard(
    ayah: AyahUiModel
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
        ) {
            Text(
                text = "Ayah ${ayah.ayahNumber}",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                color = Color(0xFF747C8C),
                fontSize = 18.sp
            )
        }

        Text(
            text = ayah.arabicText,
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF202020),
            fontSize = 38.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.End
        )

        Text(
            text = ayah.translation,
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF4E4E4E),
            fontSize = 23.sp,
            lineHeight = 32.sp
        )
    }
}

@Composable
private fun QuranReaderFooter(
    footerText: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 14.dp)
    ) {
        Text(
            text = footerText,
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF6E7482),
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )
    }
}

private fun readerSampleSurah() = AyahUiModel(
    ayahNumber = 1,
    arabicText = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
    translation = "In the name of Allah, the Entirely Merciful, the Especially Merciful.",
    surahNameEnglish = "Al-Fatihah",
    surahNameArabic = "الفاتحة",
    versesLabel = "7 verses"
)

private fun readerSampleAyahs() = listOf(
    AyahUiModel(
        ayahNumber = 1,
        arabicText = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
        translation = "In the name of Allah, the Entirely Merciful, the Especially Merciful.",
        surahNameEnglish = "Al-Fatihah",
        surahNameArabic = "الفاتحة",
        versesLabel = "7 verses"
    ),
    AyahUiModel(
        ayahNumber = 2,
        arabicText = "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ",
        translation = "All praise is due to Allah, Lord of the worlds.",
        surahNameEnglish = "Al-Fatihah",
        surahNameArabic = "الفاتحة",
        versesLabel = "7 verses"
    ),
    AyahUiModel(
        ayahNumber = 3,
        arabicText = "الرَّحْمَٰنِ الرَّحِيمِ",
        translation = "The Entirely Merciful, the Especially Merciful.",
        surahNameEnglish = "Al-Fatihah",
        surahNameArabic = "الفاتحة",
        versesLabel = "7 verses"
    )
)
