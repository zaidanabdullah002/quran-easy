package com.zaidan.quraneasy.feature.manzil.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaidan.quraneasy.core.theme.AppBackground
import com.zaidan.quraneasy.core.theme.AppPrimaryText
import com.zaidan.quraneasy.core.theme.AppSecondaryText
import com.zaidan.quraneasy.core.theme.QuranFont
import com.zaidan.quraneasy.core.ui.AppCardDefaults
import com.zaidan.quraneasy.core.ui.AppErrorView
import com.zaidan.quraneasy.core.ui.HapticIconButton
import com.zaidan.quraneasy.core.ui.hapticClick
import com.zaidan.quraneasy.feature.manzil.domain.ManzilPassage
import com.zaidan.quraneasy.feature.manzil.domain.manzilPassages

private val pageSurface = Color(0xFFFCFBF8)
private val ayahBorder = Color(0xFFEDE8DF)
private val markerText = Color(0xFF8B6F47)
private val bismillahSurface = Color(0xFFE2F1E3)
private val bismillahText = Color(0xFF315F42)
private val ayahFontSize = 34.sp
private val ayahLineGap = 60.sp
private val ayahMarkerFontSize = 22.sp
private const val ArabicVerseOpenMark = '\uFD3F'
private const val ArabicVerseCloseMark = '\uFD3E'
private const val AlFatihahSurahNumber = 1
private const val AtTawbahSurahNumber = 9
private const val BismillahText = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ"

@Preview(showBackground = true)
@Composable
private fun ManzilListPreview() {
    ManzilListContent(
        onBackClick = {},
        onPassageClick = {},
    )
}

@Composable
fun ManzilListScreen(
    onBackClick: () -> Unit,
    onPassageClick: (Int) -> Unit,
) {
    ManzilListContent(
        onBackClick = onBackClick,
        onPassageClick = onPassageClick,
    )
}

@Composable
private fun ManzilListContent(
    onBackClick: () -> Unit,
    onPassageClick: (Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        ManzilTopBar(
            title = "Manzil",
            subtitle = "Selected verses for protection",
            onBackClick = onBackClick,
        )
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            itemsIndexed(manzilPassages) { index, passage ->
                ManzilPassageCard(
                    number = index + 1,
                    passage = passage,
                    onClick = { onPassageClick(index) },
                )
            }
        }
    }
}

@Composable
private fun ManzilPassageCard(
    number: Int,
    passage: ManzilPassage,
    onClick: () -> Unit,
) {
    Card(
        onClick = hapticClick(onClick = onClick),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE8E2D8)),
        elevation = AppCardDefaults.interactiveElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 92.dp)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF1F1F1F)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = number.toString(),
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = passage.title,
                    color = AppPrimaryText,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = passage.verseRange,
                    color = AppSecondaryText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                contentDescription = null,
                tint = AppSecondaryText,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ManzilDetailPreview() {
    ManzilDetailContent(
        uiState = ManzilUiState(
            isLoading = false,
            passage = ManzilPassageUi(
                title = "Ayat al-Kursi",
                surahNumber = 2,
                surahName = "Al-Baqarah",
                startAyah = 255,
                endAyah = 255,
                ayahs = listOf(
                    ManzilAyahUi(
                        numberInSurah = 255,
                        arabic = "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ",
                        english = "God — there is no deity save Him, the Ever-Living, the Self-Subsistent.",
                    )
                ),
            ),
        ),
        onBackClick = {},
        onRetry = {},
    )
}

@Composable
fun ManzilDetailScreen(
    onBackClick: () -> Unit,
    viewModel: ManzilViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ManzilDetailContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onRetry = viewModel::load,
    )
}

@Composable
private fun ManzilDetailContent(
    uiState: ManzilUiState,
    onBackClick: () -> Unit,
    onRetry: () -> Unit,
) {
    val passage = uiState.passage
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        ManzilTopBar(
            title = passage?.title ?: "Manzil",
            subtitle = passage?.verseRange().orEmpty(),
            onBackClick = onBackClick,
        )
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = AppPrimaryText)
                }
            }

            uiState.message.isNotEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                ) {
                    AppErrorView(message = uiState.message)
                    Button(onClick = onRetry) {
                        Text("Try again")
                    }
                }
            }

            passage != null -> ManzilReading(passage = passage)
        }
    }
}

@Composable
private fun ManzilTopBar(
    title: String,
    subtitle: String,
    onBackClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF2B2B2B))
            .statusBarsPadding()
    ) {
        HapticIconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(20.dp),
            )
        }
        Column(modifier = Modifier.padding(end = 16.dp)) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (subtitle.isNotEmpty()) {
                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.78f),
                    fontSize = 13.sp,
                )
            }
        }
    }
}

@Composable
private fun ManzilReading(passage: ManzilPassageUi) {
    SelectionContainer {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(PaddingValues(horizontal = 12.dp, vertical = 18.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(pageSurface)
                    .border(BorderStroke(1.dp, ayahBorder), RoundedCornerShape(28.dp))
                    .padding(horizontal = 20.dp, vertical = 24.dp),
            ) {
                ManzilPassageBlock(passage = passage)
            }
        }
    }
}

@Composable
private fun ManzilPassageBlock(passage: ManzilPassageUi) {
    val beginsAtFirstAyah = passage.startAyah == 1
    val isAlFatihah = passage.surahNumber == AlFatihahSurahNumber && beginsAtFirstAyah
    val showBismillah = beginsAtFirstAyah && passage.surahNumber != AtTawbahSurahNumber
    val bodyAyahs = if (isAlFatihah) passage.ayahs.drop(1) else passage.ayahs

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "${passage.title} · ${passage.verseRange()}",
            color = AppPrimaryText,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
        )
        if (showBismillah) {
            BismillahHeader(ayahNumber = if (isAlFatihah) 1 else null)
        }
        Text(
            text = buildReadingText(
                ayahs = bodyAyahs,
                removeLeadingBismillah = showBismillah && !isAlFatihah,
            ),
            modifier = Modifier.fillMaxWidth(),
            color = AppPrimaryText,
            fontSize = ayahFontSize,
            fontWeight = FontWeight.Medium,
            fontFamily = QuranFont,
            textAlign = TextAlign.End,
            lineHeight = ayahLineGap,
        )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            passage.ayahs.forEach { ayah ->
                Text(
                    text = "${ayah.numberInSurah}. ${ayah.english}",
                    color = AppSecondaryText,
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                )
            }
        }
    }
}

@Composable
private fun BismillahHeader(ayahNumber: Int?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(bismillahSurface)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = buildAnnotatedString {
                append(BismillahText)
                ayahNumber?.let { number ->
                    append(' ')
                    withStyle(
                        SpanStyle(
                            color = markerText,
                            fontSize = ayahMarkerFontSize,
                            fontFamily = FontFamily.Default,
                        )
                    ) {
                        append(number.toArabicIndicDigits())
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            color = bismillahText,
            fontSize = 31.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = QuranFont,
            textAlign = TextAlign.Center,
            lineHeight = 46.sp,
        )
    }
}

private fun buildReadingText(
    ayahs: List<ManzilAyahUi>,
    removeLeadingBismillah: Boolean,
) = buildAnnotatedString {
    ayahs.forEachIndexed { index, ayah ->
        val ayahText = if (index == 0 && removeLeadingBismillah) {
            ayah.arabic.withoutProviderBismillahPrefix()
        } else {
            ayah.arabic.trim()
        }
        append(ayahText)
        append(' ')
        withStyle(
            SpanStyle(
                color = markerText,
                fontSize = ayahMarkerFontSize,
                fontFamily = FontFamily.Default,
            )
        ) {
            append(ArabicVerseOpenMark)
            append(ayah.numberInSurah.toArabicIndicDigits())
            append(ArabicVerseCloseMark)
        }
        if (index != ayahs.lastIndex) {
            append(' ')
        }
    }
}

private fun ManzilPassageUi.verseRange(): String {
    return if (startAyah == endAyah) {
        "$surahNumber:$startAyah"
    } else {
        "$surahNumber:$startAyah–$endAyah"
    }
}

private fun String.withoutProviderBismillahPrefix(): String {
    val providerWords = trim().split(Regex("\\s+"), limit = 5)
    return providerWords.getOrElse(4) { trim() }
}

private fun Int.toArabicIndicDigits(): String = toString().map { digit ->
    if (digit in '0'..'9') {
        ('\u0660'.code + (digit - '0')).toChar()
    } else {
        digit
    }
}.joinToString(separator = "")
