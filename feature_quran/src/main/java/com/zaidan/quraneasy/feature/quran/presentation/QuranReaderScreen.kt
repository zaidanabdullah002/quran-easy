package com.zaidan.quraneasy.feature.quran.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.FavoriteBorder
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
import com.zaidan.quraneasy.feature.quran.presentation.model.AyahUiModel
import com.zaidan.quraneasy.feature.quran.presentation.model.ReaderHeaderUiModel

private val ReaderBackground = Color(0xFFF3F3F3)
private val ReaderTopBar = Color(0xFF2B2B2B)
private val FooterBorder = Color(0xFFD8D8D8)

@Preview(showBackground = true)
@Composable
private fun QuranReaderScreenPreview() {
    QuranReaderScreen(
        onBackClick = {},
        readerType = ReaderType.SURAH.ordinal,
        itemNumber = 0
    )
}

@Composable
fun QuranReaderScreen(
    onBackClick: () -> Unit,
    readerType: Int,
    itemNumber: Int = 1

) {
    val title = if(readerType == ReaderType.SURAH.ordinal){
        ReaderHeaderUiModel(
            title = "Al-Fatihah",
            subtitle = "7 verses",
            arabicName = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ"
        )
    }else{
        ReaderHeaderUiModel(
            title = "Juz-1",
            subtitle = "7 verses",
            arabicName = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ReaderBackground)
    ) {
         QuranReaderTopBar(
            title = title,
            onBackClick = onBackClick
        )
        val ayahs = listOf(
            AyahUiModel(0,0,"s"),
            AyahUiModel(1,1,"s")
        )
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(ayahs) { ayah ->
                QuranAyahCard(ayah = ayah)
            }
        }

        QuranReaderFooter(
            footerText = "Surah ${itemNumber}"
        )
    }
}

@Composable
private fun QuranReaderTopBar(
    title: ReaderHeaderUiModel,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ReaderTopBar)
            .statusBarsPadding()
            .padding(horizontal = 14.dp, vertical = 16.dp)
    ) {
        RowCenteredTopBar(
            onBackClick = onBackClick,
            title = title
        )
    }
}

@Composable
private fun RowCenteredTopBar(
    onBackClick: () -> Unit,
    title: ReaderHeaderUiModel
) {
    androidx.compose.foundation.layout.Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                contentDescription = "Back",
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.padding(start = 4.dp))

        Icon(
            imageVector = Icons.Outlined.FavoriteBorder,
            contentDescription = null,
            tint = Color.White
        )

        Spacer(modifier = Modifier.padding(start = 8.dp))

        Column(modifier = Modifier.fillMaxWidth(0.62f)) {
            Text(
                text = title.title,
                color = Color.White,
                fontSize = 23.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "${title.subtitle}",
                color = Color.White.copy(alpha = 0.78f),
                fontSize = 16.sp
            )
        }

        Text(
            text = title.arabicName,
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp,
            pressedElevation = 6.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = ayah.arabicText,
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF202020),
                fontSize = 32.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.End,
                lineHeight = 48.sp
            )
            ayah.translation?.let {
                Text(
                    text = ayah.translation,
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF4E4E4E),
                    fontSize = 18.sp,
                    lineHeight = 28.sp
                )
            }

        }
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

