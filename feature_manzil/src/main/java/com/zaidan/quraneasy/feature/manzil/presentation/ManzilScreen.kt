package com.zaidan.quraneasy.feature.manzil.presentation

import com.zaidan.quraneasy.feature.quran.presentation.audio.QuranAudioRibbon
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import com.zaidan.quraneasy.feature.quran.presentation.audio.ReaderAudioUiState
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
import com.zaidan.quraneasy.core.settings.ReaderSettingsEffects
import com.zaidan.quraneasy.core.theme.LocalQuranTypography
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
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
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
            item {
                Surface(color = colors.surfaceContainerLow, shape = RoundedCornerShape(16.dp)) {
                    Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("SPIRITUAL SHIELD · MANZIL", color = colors.secondary, fontWeight = FontWeight.Bold)
                        Text("Selected Quranic passages for daily recitation, remembrance, and reflection.",
                            style = MaterialTheme.typography.bodyMedium, color = colors.onSurfaceVariant)
                        Text("${manzilPassages.size} passages · ${manzilPassages.sumOf { it.endAyah - it.startAyah + 1 }} verses",
                            style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
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
    val colors = MaterialTheme.colorScheme
    Card(
        onClick = hapticClick(onClick = onClick),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceContainerLowest),
        border = BorderStroke(1.dp, colors.outlineVariant),
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
                    .size(40.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(colors.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = number.toString(),
                    color = colors.onSurface,
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
                    color = colors.onSurface,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = passage.verseRange,
                    color = colors.onSurfaceVariant,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                contentDescription = null,
                tint = colors.onSurfaceVariant,
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
    ReaderSettingsEffects()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val audioState by viewModel.audioState.collectAsStateWithLifecycle()
    LaunchedEffect(uiState) { viewModel.prepareAudio() }
    DisposableEffect(viewModel) { onDispose(viewModel::stopAudio) }
    ManzilDetailContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onRetry = viewModel::load,
        audioState = audioState,
        onToggleAudio = viewModel::toggleAudio,
        onPreviousAyah = viewModel::previousAyah,
        onNextAyah = viewModel::nextAyah,
    )
}

@Composable
private fun ManzilDetailContent(
    uiState: ManzilUiState,
    onBackClick: () -> Unit,
    onRetry: () -> Unit,
    audioState: ReaderAudioUiState = ReaderAudioUiState(),
    onToggleAudio: () -> Unit = {},
    onPreviousAyah: () -> Unit = {},
    onNextAyah: () -> Unit = {},
) {
    val passage = uiState.passage
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        if (passage != null) ManzilTopBar(
            title = "Daily Manzil",
            subtitle = "Continuous reading",
            onBackClick = onBackClick,
        )
        Box(Modifier.weight(1f)) {
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(color = colors.primary)
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

                uiState.passages.isNotEmpty() -> ManzilReading(uiState.passages)
                passage != null -> ManzilReading(listOf(passage))
            }
        }
        val passages = uiState.passages.ifEmpty { listOfNotNull(passage) }
        if (!uiState.isLoading && uiState.message.isEmpty() && passages.isNotEmpty()) {
            ManzilAudioControls(passages, audioState, onToggleAudio, onPreviousAyah, onNextAyah)
        }
    }
}

@Composable
private fun ManzilTopBar(
    title: String,
    subtitle: String,
    onBackClick: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 8.dp)
    ) {
        HapticIconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                contentDescription = "Back",
                tint = colors.onSurface,
                modifier = Modifier.size(20.dp),
            )
        }
        Column(modifier = Modifier.padding(end = 16.dp)) {
            Text(
                text = title,
                color = colors.onSurface,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (subtitle.isNotEmpty()) {
                Text(
                    text = subtitle,
                    color = colors.onSurface.copy(alpha = 0.78f),
                    fontSize = 13.sp,
                )
            }
        }
    }
}

@Composable
private fun ManzilReading(passages: List<ManzilPassageUi>) {
    val colors = MaterialTheme.colorScheme
    val typography = LocalQuranTypography.current
    SelectionContainer {
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item {
                Surface(color = colors.surfaceContainerLowest, contentColor = colors.onSurface,
                    border = BorderStroke(1.dp, colors.outlineVariant), shape = RoundedCornerShape(16.dp)) {
                    Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("DAILY MANZIL", color = colors.secondary, style = MaterialTheme.typography.labelLarge)
                        Text("Continuous recitation", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = colors.onSurface)
                        Text("${passages.size} passages · ${passages.sumOf { it.ayahs.size }} verses",
                            color = colors.onSurfaceVariant)
                    }
                }
            }
            itemsIndexed(passages, key = { index, passage -> "${passage.surahNumber}:${passage.startAyah}:$index" }) { index, passage ->
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "${passage.surahName} · ${passage.verseRange()}",
                        color = colors.secondary,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(horizontal = 4.dp),
                    )
                    val hasUnnumberedBismillah = passage.startAyah == 1 &&
                        passage.surahNumber !in setOf(AlFatihahSurahNumber, AtTawbahSurahNumber)
                    // Al-Fatihah already contains Bismillah as its numbered first ayah.
                    // Show no additional invocations between continuous Manzil passages.
                    if (index == 0 && hasUnnumberedBismillah) BismillahHeader(null)
                    Text(
                        text = buildReadingText(passage.ayahs, removeLeadingBismillah = hasUnnumberedBismillah),
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                        fontFamily = typography.fontFamily,
                        fontSize = typography.ayahFontSize,
                        lineHeight = typography.ayahLineHeight,
                        textAlign = TextAlign.End,
                        color = colors.onSurface,
                    )
                    androidx.compose.material3.HorizontalDivider(color = colors.outlineVariant.copy(alpha = 0.55f))
                }
            }
        }
    }
}

@Composable
private fun BismillahHeader(ayahNumber: Int?) {
    val typography = LocalQuranTypography.current
    val colors = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(colors.surfaceContainerLowest)
            .border(1.dp, colors.outlineVariant, RoundedCornerShape(20.dp))
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
                            color = colors.primary,
                            fontSize = typography.verseMarkerFontSize,
                            fontFamily = FontFamily.Default,
                        )
                    ) {
                        append(number.toArabicIndicDigits())
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            color = colors.onSecondaryContainer,
            fontSize = typography.bismillahFontSize,
            fontWeight = FontWeight.Medium,
            fontFamily = typography.fontFamily,
            textAlign = TextAlign.Center,
            lineHeight = typography.bismillahLineHeight,
        )
    }
}

@Composable
private fun buildReadingText(
    ayahs: List<ManzilAyahUi>,
    removeLeadingBismillah: Boolean,
) : androidx.compose.ui.text.AnnotatedString {
    val typography = LocalQuranTypography.current
    val markerColor = MaterialTheme.colorScheme.primary
    return buildAnnotatedString {
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
                color = markerColor,
                    fontSize = typography.verseMarkerFontSize,
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

@Composable
private fun ManzilAudioControls(
    passages: List<ManzilPassageUi>,
    state: ReaderAudioUiState,
    onToggle: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
) {
    val playlist = passages.toAudioPlaylist()
    val current = playlist.getOrNull(state.currentIndex) ?: return
    val passage = passages.first { it.surahNumber == current.surahNumber &&
        current.ayahNumber in it.startAyah..it.endAyah }
    QuranAudioRibbon(
        titleLabel = passage.surahName,
        positionLabel = "${current.surahNumber}:${current.ayahNumber} · ${state.currentIndex + 1}/${playlist.size}",
        totalAyahs = playlist.size,
        audioUiState = state,
        onToggleAudio = onToggle,
        onPreviousAyah = onPrevious,
        onNextAyah = onNext,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
    )
}
