package com.zaidan.quraneasy.feature.quran.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaidan.quraneasy.core.theme.AppBackground
import com.zaidan.quraneasy.core.theme.AppPrimaryText
import com.zaidan.quraneasy.core.theme.QuranFont
import com.zaidan.quraneasy.core.ui.AppErrorView
import com.zaidan.quraneasy.core.ui.AppLoadingView
import com.zaidan.quraneasy.core.ui.HapticIconButton
import com.zaidan.quraneasy.feature.quran.presentation.model.AyahUiModel
import com.zaidan.quraneasy.feature.quran.presentation.model.AyahUiState
import com.zaidan.quraneasy.feature.quran.presentation.model.ReaderHeaderUiModel
import com.zaidan.quraneasy.feature.quran.presentation.model.dummyData.ayahList
import com.zaidan.quraneasy.feature.quran.presentation.viewmodel.QuranReaderViewModel
import my.nanihadesuka.compose.LazyColumnScrollbar

private val ReaderBackground = AppBackground
private val ReaderTopBar = Color(0xFF2B2B2B)
private val ReaderPageSurface = Color(0xFFFCFBF8)
private val ReaderAyahBorder = Color(0xFFEDE8DF)
private val ReaderAyahText = AppPrimaryText
private val ReaderMarkerText = Color(0xFF8B6F47)
private val ayahLineGap = 60.sp
private val ayahFontSize = 34.sp
private val ayahMarkerFontSize = 22.sp
private const val ArabicVerseOpenMark = '\uFD3F'
private const val ArabicVerseCloseMark = '\uFD3E'

@Preview(showBackground = true)
@Composable
private fun QuranReaderScreenPreview() {
    QuranReaderContent(
        ayahUiState = AyahUiState(
            isLoading = false,
            isReady = true,
            ayahs = ayahList
        ),
        title = ReaderHeaderUiModel(
            title = "Surah 1",
            subtitle = "2 verses",
            arabicName = "الفاتحة"
        ),
        onBackClick = {}
    )
}

@Composable
fun QuranReaderScreen(
    onBackClick: () -> Unit,
    readerType: Int,
    itemNumber: Int = 1,
    quranReaderViewModel: QuranReaderViewModel
) {
    val ayahUiState by quranReaderViewModel.ayahUiState.collectAsState()

    LaunchedEffect(readerType, itemNumber) {
        when (readerType) {
            ReaderType.SURAH.ordinal -> quranReaderViewModel.loadAyahWithSurahNumber(itemNumber)
            ReaderType.JUZ.ordinal -> quranReaderViewModel.loadAyahWithJuzNumber(itemNumber)
        }
    }
    val title = if (readerType == ReaderType.SURAH.ordinal) {
        ReaderHeaderUiModel(
            title = "Surah $itemNumber",
            subtitle = "${ayahUiState.ayahs.size} verses",
            arabicName = " "
        )
    } else {
        ReaderHeaderUiModel(
            title = "Juz-$itemNumber",
            subtitle = "${ayahUiState.ayahs.size} verses",
            arabicName = " "
        )
    }
    QuranReaderContent(
        ayahUiState = ayahUiState,
        title = title,
        onBackClick = onBackClick
    )
}

@Composable
private fun QuranReaderContent(
    ayahUiState: AyahUiState,
    title: ReaderHeaderUiModel,
    onBackClick: () -> Unit
) {
    val readerListState = rememberLazyListState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ReaderBackground)
    ) {
        QuranReaderTopBar(
            title = title,
            onBackClick = onBackClick
        )

        if (ayahUiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.84f),
                contentAlignment = Alignment.Center
            ) {
                AppLoadingView(
                    title = if (title.title.startsWith("Juz")) {
                        "Downloading juz content"
                    } else {
                        "Downloading surah content"
                    },
                    subtitle = if (title.title.startsWith("Juz")) {
                        "Preparing this juz for smooth offline reading"
                    } else {
                        "Preparing this surah for smooth offline reading"
                    }
                )
            }
        } else if (ayahUiState.message.isNullOrBlank().not()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                AppErrorView(message = ayahUiState.message)
            }
        } else {
            LazyColumnScrollbar(
                state = readerListState,
                settings = quranScrollbarSettings(),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                LazyColumn(
                    state = readerListState,
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        QuranReadingSheet(ayahs = ayahUiState.ayahs)
                    }
                }
            }
        }
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
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        HapticIconButton(onClick = onBackClick) {
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

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title.title,
                color = Color.White,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = title.subtitle,
                color = Color.White.copy(alpha = 0.78f),
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Text(
            text = title.arabicName,
            modifier = Modifier.padding(start = 12.dp),
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun QuranReadingSheet(
    ayahs: List<AyahUiModel>
) {
    SelectionContainer {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(28.dp))
                .background(ReaderPageSurface)
                .border(BorderStroke(1.dp, ReaderAyahBorder), RoundedCornerShape(28.dp))
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Text(
                text = buildReadingSheetText(ayahs),
                modifier = Modifier.fillMaxWidth(),
                color = ReaderAyahText,
                fontSize = ayahFontSize,
                fontWeight = FontWeight.Medium,
                fontFamily = QuranFont,
                textAlign = TextAlign.End,
                lineHeight = ayahLineGap
            )
        }
    }
}

private fun buildReadingSheetText(ayahs: List<AyahUiModel>) = buildAnnotatedString {
    ayahs.forEachIndexed { index, ayah ->
        append(ayah.arabicText.trim())
        append(' ')
        pushStyle(
            SpanStyle(
                color = ReaderMarkerText,
                fontSize = ayahMarkerFontSize,
                fontFamily = FontFamily.Default
            )
        )
        append(ArabicVerseOpenMark)
        append(ayah.numberInSurah.toArabicIndicDigits())
        append(ArabicVerseCloseMark)
        pop()

        if (index != ayahs.lastIndex) {
            append(' ')
        }
    }
}

private fun Int.toArabicIndicDigits(): String = toString().map { digit ->
    if (digit in '0'..'9') {
        ('\u0660'.code + (digit - '0')).toChar()
    } else {
        digit
    }
}.joinToString(separator = "")
