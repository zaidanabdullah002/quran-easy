package com.zaidan.quraneasy.feature.quran.presentation

import com.zaidan.quraneasy.feature.quran.presentation.audio.QuranAudioRibbon
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.FormatSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaidan.quraneasy.core.settings.ReaderSettingsEffects
import com.zaidan.quraneasy.core.theme.AmiriQuranFont
import com.zaidan.quraneasy.core.theme.LocalQuranTypography
import com.zaidan.quraneasy.core.ui.AppErrorView
import com.zaidan.quraneasy.core.ui.AppSkeletonLine
import com.zaidan.quraneasy.core.ui.HapticIconButton
import com.zaidan.quraneasy.feature.quran.presentation.audio.ReaderAudioUiState
import com.zaidan.quraneasy.feature.quran.presentation.model.AyahUiModel
import com.zaidan.quraneasy.feature.quran.presentation.model.AyahUiState
import com.zaidan.quraneasy.feature.quran.presentation.model.ReaderHeaderUiModel
import com.zaidan.quraneasy.feature.quran.presentation.model.SurahRevelationOrder
import com.zaidan.quraneasy.feature.quran.presentation.model.dummyData.ayahList
import com.zaidan.quraneasy.feature.quran.presentation.viewmodel.QuranReaderViewModel
import my.nanihadesuka.compose.LazyColumnScrollbar

private const val AlFatihahSurahNumber = 1
private const val AtTawbahSurahNumber = 9
private const val BismillahText = "بِسْمِ اللَّهِ الرَّحْمَـٰنِ الرَّحِيمِ"

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
        onBackClick = {},
        onBookmarkClick = {}
    )
}

@Composable
fun QuranReaderScreen(
    onBackClick: () -> Unit,
    readerType: Int,
    itemNumber: Int = 1,
    quranReaderViewModel: QuranReaderViewModel
) {
    ReaderSettingsEffects()
    val ayahUiState by quranReaderViewModel.ayahUiState.collectAsState()
    val surahs by quranReaderViewModel.surahs.collectAsState()
    val audioUiState by quranReaderViewModel.audioUiState.collectAsState()

    LaunchedEffect(readerType, itemNumber) {
        when (readerType) {
            ReaderType.SURAH.ordinal -> quranReaderViewModel.loadAyahWithSurahNumber(itemNumber)
            ReaderType.JUZ.ordinal -> quranReaderViewModel.loadAyahWithJuzNumber(itemNumber)
        }
    }
    LaunchedEffect(ayahUiState.ayahs) {
        quranReaderViewModel.setAudioPlaylist(ayahUiState.ayahs)
    }
    DisposableEffect(quranReaderViewModel) {
        onDispose(quranReaderViewModel::stopAudio)
    }
    val title = if (readerType == ReaderType.SURAH.ordinal) {
        val surah = surahs.firstOrNull { it.number == itemNumber }
        ReaderHeaderUiModel(
            title = surah?.let { displaySurahName(it.number, it.englishName) } ?: "Surah $itemNumber",
            subtitle = buildString {
                append(surah?.verses ?: ayahUiState.ayahs.size)
                append(" verses")
            },
            arabicName = surah?.arabicName?.let(::displayArabicSurahName).orEmpty(),
            translation = surah?.translation.orEmpty(),
            revelationType = surah?.revelationType.orEmpty(),
            juzNumber = ayahUiState.ayahs.firstOrNull()?.juzNumber ?: 0,
            revelationOrder = SurahRevelationOrder.getOrElse(itemNumber) { 0 }
        )
    } else {
        ReaderHeaderUiModel(
            title = "Juz-$itemNumber",
            subtitle = "${ayahUiState.ayahs.size} verses",
            arabicName = " ",
            juzNumber = itemNumber,
        )
    }
    QuranReaderContent(
        ayahUiState = ayahUiState,
        title = title,
        onBackClick = onBackClick,
        onBookmarkClick = quranReaderViewModel::toggleBookmark,
        audioUiState = audioUiState,
        onToggleAudio = quranReaderViewModel::toggleAudioPlayback,
        onPreviousAyah = quranReaderViewModel::playPreviousAyah,
        onNextAyah = quranReaderViewModel::playNextAyah,
        onAyahClick = quranReaderViewModel::playAyahAtIndex,
        onTextSizeClick = quranReaderViewModel::cycleTextScale,
    )
}

@Composable
private fun QuranReaderContent(
    ayahUiState: AyahUiState,
    title: ReaderHeaderUiModel,
    onBackClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    audioUiState: ReaderAudioUiState = ReaderAudioUiState(),
    onToggleAudio: () -> Unit = {},
    onPreviousAyah: () -> Unit = {},
    onNextAyah: () -> Unit = {},
    onAyahClick: (Int) -> Unit = {},
    onTextSizeClick: () -> Unit = {}
) {
    val readerListState = rememberLazyListState()
    val colors = MaterialTheme.colorScheme
    val darkTheme = colors.background.luminance() < 0.2f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (darkTheme) Color.Black else colors.background)
    ) {
        QuranReaderAppBar(
            onBackClick = onBackClick,
            isBookmarked = ayahUiState.isBookmarked,
            onBookmarkClick = onBookmarkClick,
            isDark = darkTheme
        )

        SurahReaderMetaBar(
            title = title,
            isBookmarked = ayahUiState.isBookmarked,
            onBookmarkClick = onBookmarkClick,
            onTextSizeClick = onTextSizeClick,
            isDark = darkTheme
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (ayahUiState.isLoading) {
                ReaderSkeleton(modifier = Modifier.fillMaxSize())
            } else if (!ayahUiState.message.isNullOrBlank()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    AppErrorView(message = ayahUiState.message)
                }
            } else {
                LazyColumnScrollbar(
                    state = readerListState,
                    settings = quranScrollbarSettings(),
                    modifier = Modifier.fillMaxSize()
                ) {
                    LazyColumn(
                        state = readerListState,
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            top = 16.dp,
                            end = 16.dp,
                            bottom = 120.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            QuranReadingSheet(
                                ayahs = ayahUiState.ayahs,
                                title = title,
                                activeAyah = ayahUiState.ayahs.getOrNull(audioUiState.currentIndex),
                                onAyahClick = onAyahClick,
                                isDark = darkTheme
                            )
                        }
                    }
                }
            }

            QuranAudioRibbon(
                titleLabel = if (title.title.startsWith("Juz") || title.title.startsWith("Surah ")) title.title else "Surah ${title.title}",
                positionLabel = "Ayah ${ayahUiState.ayahs.getOrNull(audioUiState.currentIndex)?.numberInSurah ?: (audioUiState.currentIndex + 1)} of ${ayahUiState.ayahs.size}",
                totalAyahs = ayahUiState.ayahs.size,
                audioUiState = audioUiState,
                onToggleAudio = onToggleAudio,
                onPreviousAyah = onPreviousAyah,
                onNextAyah = onNextAyah,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }
    }
}

@Composable
private fun ReaderSkeleton(
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(colors.surfaceContainerLowest)
                    .border(BorderStroke(1.dp, colors.outlineVariant), RoundedCornerShape(24.dp))
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    repeat(7) { index ->
                        AppSkeletonLine(
                            modifier = Modifier
                                .fillMaxWidth(
                                    when (index % 3) {
                                        0 -> 0.96f
                                        1 -> 0.88f
                                        else -> 0.78f
                                    }
                                )
                                .height(34.dp),
                            tint = colors.surfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuranReaderAppBar(
    onBackClick: () -> Unit,
    isBookmarked: Boolean,
    onBookmarkClick: () -> Unit,
    isDark: Boolean
) {
    val colors = MaterialTheme.colorScheme
    val barBg = if (isDark) Color(0xFF0C1310).copy(alpha = 0.95f) else colors.background
    val accentTeal = Color(0xFF2DD4BF)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(barBg)
            .statusBarsPadding()
            .height(56.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HapticIconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = if (isDark) Color(0xFF94A3B8) else colors.onSurface
            )
        }

        Spacer(modifier = Modifier.size(6.dp))

        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.AutoStories,
                contentDescription = null,
                tint = if (isDark) accentTeal else colors.secondary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = "Quran Reader",
                color = if (isDark) Color.White else colors.onSurface,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        HapticIconButton(onClick = onBookmarkClick) {
            AnimatedBookmarkHeart(isBookmarked = isBookmarked)
        }

        Spacer(modifier = Modifier.size(6.dp))

        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .then(
                    if (isDark) {
                        Modifier
                            .background(Color(0xFF111C17))
                            .border(1.dp, Color(0xFF10B981).copy(alpha = 0.3f), CircleShape)
                    } else {
                        Modifier.background(colors.primary)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = "Profile",
                tint = if (isDark) accentTeal else colors.onPrimary,
                modifier = Modifier.size(19.dp)
            )
        }
    }
}

@Composable
private fun SurahReaderMetaBar(
    title: ReaderHeaderUiModel,
    isBookmarked: Boolean,
    onBookmarkClick: () -> Unit,
    onTextSizeClick: () -> Unit,
    isDark: Boolean
) {
    val colors = MaterialTheme.colorScheme
    val barBg = if (isDark) Color(0xFF000000) else colors.surfaceContainerLow
    val borderBottom = if (isDark) Color(0xFF10B981).copy(alpha = 0.15f) else colors.outlineVariant

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(barBg)
            .border(
                BorderStroke(1.dp, borderBottom)
            )
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (title.title.startsWith("Surah ")) title.title else "Surah ${title.title}",
                    color = if (isDark) Color.White else colors.onSurface,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
                if (title.revelationType.isNotBlank()) {
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = title.revelationType.uppercase(),
                        color = if (isDark) Color(0xFF6EE7B7) else colors.primary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(if (isDark) Color(0xFF10B981).copy(alpha = 0.15f) else colors.primaryContainer)
                            .border(
                                1.dp,
                                if (isDark) Color(0xFF10B981).copy(alpha = 0.3f) else colors.primary.copy(alpha = 0.45f),
                                RoundedCornerShape(50)
                            )
                            .padding(horizontal = 9.dp, vertical = 3.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = buildString {
                    if (title.translation.isNotBlank()) append(title.translation).append(" · ")
                    append(title.subtitle.replace(" verses", " Verses"))
                    if (title.juzNumber > 0) append(" · Juz ").append(title.juzNumber)
                    if (title.revelationOrder > 0) append(" · Revelation ").append(title.revelationOrder)
                },
                color = if (isDark) Color(0xFF94A3B8) else colors.onSurfaceVariant,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Bookmark circle action
        HapticIconButton(
            onClick = onBookmarkClick,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .then(
                    if (isBookmarked) {
                        if (isDark) {
                            Modifier.background(
                                Brush.linearGradient(listOf(Color(0xFF06B6D4), Color(0xFF10B981)))
                            )
                        } else {
                            Modifier.background(colors.secondary)
                        }
                    } else {
                        if (isDark) {
                            Modifier
                                .background(Color(0xFF0C1310))
                                .border(1.dp, Color(0xFF10B981).copy(alpha = 0.25f), CircleShape)
                        } else {
                            Modifier
                                .background(colors.surfaceContainer)
                                .border(1.dp, colors.outlineVariant, CircleShape)
                        }
                    }
                )
        ) {
            Icon(
                imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                contentDescription = "Bookmark Surah",
                tint = if (isBookmarked) {
                    if (isDark) Color.Black else Color.White
                } else {
                    if (isDark) Color(0xFF6EE7B7) else colors.onSurfaceVariant
                },
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.size(8.dp))

        // Text scale adjustment circle action
        HapticIconButton(
            onClick = onTextSizeClick,
            enabled = true,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .then(
                    if (isDark) {
                        Modifier
                            .background(Color(0xFF0C1310))
                            .border(1.dp, Color(0xFF10B981).copy(alpha = 0.25f), CircleShape)
                    } else {
                        Modifier
                            .background(colors.surfaceContainerLow)
                            .border(1.dp, colors.outlineVariant, CircleShape)
                    }
                )
        ) {
            Icon(
                imageVector = Icons.Outlined.FormatSize,
                contentDescription = "Adjust font size",
                tint = if (isDark) Color(0xFF6EE7B7) else colors.onSurface,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun AnimatedBookmarkHeart(
    isBookmarked: Boolean
) {
    val scale = remember { Animatable(1f) }

    LaunchedEffect(isBookmarked) {
        if (isBookmarked) {
            scale.snapTo(0.82f)
            scale.animateTo(
                targetValue = 1.22f,
                animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing)
            )
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 140, easing = FastOutSlowInEasing)
            )
        } else {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 120, easing = FastOutSlowInEasing)
            )
        }
    }

    Icon(
        imageVector = if (isBookmarked) {
            Icons.Filled.Bookmark
        } else {
            Icons.Outlined.BookmarkBorder
        },
        contentDescription = "Bookmark",
        tint = if (isBookmarked) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.scale(scale.value)
    )
}

@Composable
private fun QuranReadingSheet(
    ayahs: List<AyahUiModel>,
    title: ReaderHeaderUiModel,
    activeAyah: AyahUiModel? = null,
    onAyahClick: (Int) -> Unit = {},
    isDark: Boolean = true
) {
    val typography = LocalQuranTypography.current
    val colors = MaterialTheme.colorScheme
    val sheetBg = if (isDark) Color(0xFF000000) else Color(0xFFFFFFFF)
    val sheetBorder = if (isDark) Color(0xFF10B981).copy(alpha = 0.25f) else colors.outlineVariant

    SelectionContainer {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(sheetBg)
                .border(BorderStroke(1.dp, sheetBorder), RoundedCornerShape(20.dp))
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ayahs.groupBy { it.surahNumber }.values.forEach { surahAyahs ->
                val firstAyah = surahAyahs.firstOrNull()
                val beginsAtFirstAyah = firstAyah?.numberInSurah == 1
                val isAlFatihah = firstAyah?.surahNumber == AlFatihahSurahNumber
                val showBismillah = beginsAtFirstAyah &&
                    firstAyah.surahNumber != AtTawbahSurahNumber
                val bodyAyahs = if (isAlFatihah && beginsAtFirstAyah) {
                    surahAyahs.drop(1)
                } else {
                    surahAyahs
                }

                if (showBismillah) {
                    BismillahHeader(
                        isDark = isDark,
                        ayahNumber = if (isAlFatihah) 1 else null,
                    )
                }

                val inlineContent = remember(bodyAyahs, isDark) {
                    bodyAyahs.associate { ayah ->
                        "marker_${ayah.numberInSurah}" to InlineTextContent(
                            placeholder = Placeholder(
                                width = 32.sp,
                                height = 32.sp,
                                placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                            )
                        ) {
                            AyahMarkerMedallion(
                                ayahNumber = ayah.numberInSurah,
                                isDark = isDark
                            )
                        }
                    }
                }

                var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
                val annotatedText = buildReadingSheetText(
                    ayahs = bodyAyahs,
                    removeLeadingBismillah = showBismillah && !isAlFatihah,
                    activeAyah = activeAyah,
                    isDark = isDark
                )

                Text(
                    text = annotatedText,
                    inlineContent = inlineContent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .pointerInput(bodyAyahs) {
                            detectTapGestures { offset ->
                                textLayoutResult?.let { layout ->
                                    val position = layout.getOffsetForPosition(offset)
                                    annotatedText.getStringAnnotations(tag = "AYAH", start = position, end = position)
                                        .firstOrNull()?.let { annotation ->
                                            val indexInBody = annotation.item.toIntOrNull()
                                            if (indexInBody != null) {
                                                val globalIndex = ayahs.indexOfFirst {
                                                    it.surahNumber == bodyAyahs[indexInBody].surahNumber &&
                                                        it.numberInSurah == bodyAyahs[indexInBody].numberInSurah
                                                }
                                                if (globalIndex >= 0) onAyahClick(globalIndex)
                                            }
                                        }
                                }
                            }
                        },
                    onTextLayout = { textLayoutResult = it },
                    color = if (isDark) Color(0xFFFFFFFF) else Color(0xFF211E1C),
                    fontSize = typography.ayahFontSize,
                    fontWeight = FontWeight.Normal,
                    fontFamily = typography.fontFamily,
                    textAlign = TextAlign.End,
                    lineHeight = typography.ayahLineHeight
                )

                ReaderPageFooter(
                    title = title,
                    ayahs = surahAyahs,
                    isDark = isDark
                )
            }
        }
    }
}

@Composable
fun AyahMarkerMedallion(
    ayahNumber: Int,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val outerColor = if (isDark) Color(0xFF2DD4BF) else Color(0xFFC86D51)
    val innerFill = if (isDark) Color(0xFF10B981).copy(alpha = 0.18f) else Color(0xFFC86D51).copy(alpha = 0.12f)
    val innerStroke = if (isDark) Color(0xFF6EE7B7) else Color(0xFFB4583C)
    val textColor = if (isDark) Color(0xFFD4A359) else Color(0xFF8F4129)

    Box(
        modifier = modifier.size(28.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val outerRadius = size.minDimension / 2f - 1.dp.toPx()
            val innerRadius = outerRadius - 2.5.dp.toPx()

            // Outer dashed ring
            drawCircle(
                color = outerColor,
                radius = outerRadius,
                center = center,
                style = Stroke(
                    width = 1.2.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 3.5f))
                )
            )

            // Inner filled circle
            drawCircle(
                color = innerFill,
                radius = innerRadius,
                center = center
            )

            // Inner border
            drawCircle(
                color = innerStroke,
                radius = innerRadius,
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )
        }

        Text(
            text = ayahNumber.toArabicIndicDigits(),
            fontFamily = AmiriQuranFont,
            fontSize = if (ayahNumber >= 100) 9.sp else 11.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun BismillahHeader(
    isDark: Boolean,
    ayahNumber: Int? = null,
) {
    val typography = LocalQuranTypography.current
    val arabicColor = if (isDark) Color.White else Color(0xFF211E1C)
    val borderColor = if (isDark) Color(0xFF2DD4BF).copy(alpha = 0.3f) else Color(0xFFE8DFC9)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 92.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .then(
                if (isDark) {
                    Modifier.background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF041A15), Color(0xFF092E25), Color(0xFF041A15))
                        )
                    )
                } else {
                    Modifier.background(Color(0xFFF5F0E6))
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            Text(
                text = BismillahText,
                modifier = Modifier.fillMaxWidth(),
                color = arabicColor,
                fontSize = typography.bismillahFontSize,
                fontWeight = FontWeight.Normal,
                fontFamily = typography.fontFamily,
                textAlign = TextAlign.Center,
                lineHeight = typography.bismillahLineHeight
            )
        }
    }
}

@Composable
private fun ReaderPageFooter(
    title: ReaderHeaderUiModel,
    ayahs: List<AyahUiModel>,
    isDark: Boolean
) {
    val colors = MaterialTheme.colorScheme
    if (ayahs.isEmpty()) return
    val dividerColor = if (isDark) Color(0xFF10B981).copy(alpha = 0.15f) else Color(0xFFE8DFC9)
    val dotColor = if (isDark) Color(0xFF2DD4BF) else Color(0xFFC86D51)

    HorizontalDivider(
        modifier = Modifier.padding(top = 8.dp),
        color = dividerColor
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${title.title} (${ayahs.first().numberInSurah}–${ayahs.last().numberInSurah})",
            color = if (isDark) Color(0xFF94A3B8) else Color(0xFF7C8B80),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
        val lastAyah = ayahs.last()
        val pageLabel = if (lastAyah.pageNumber > 0) "Page ${lastAyah.pageNumber}" else "Continuous reading"
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
            Spacer(modifier = Modifier.size(6.dp))
            Text(
                text = buildString {
                    if (lastAyah.juzNumber > 0) append("Juz ${lastAyah.juzNumber} · ")
                    append(pageLabel)
                },
                color = if (isDark) Color(0xFFF1F5F9) else Color(0xFF211E1C),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun buildReadingSheetText(
    ayahs: List<AyahUiModel>,
    removeLeadingBismillah: Boolean = false,
    activeAyah: AyahUiModel? = null,
    isDark: Boolean = true
): androidx.compose.ui.text.AnnotatedString {
    return buildAnnotatedString {
        ayahs.forEachIndexed { index, ayah ->
            val ayahText = if (index == 0 && removeLeadingBismillah) {
                ayah.arabicText.withoutProviderBismillahPrefix()
            } else {
                ayah.arabicText.trim()
            }
            val isActive = ayah.surahNumber == activeAyah?.surahNumber &&
                ayah.numberInSurah == activeAyah?.numberInSurah

            pushStringAnnotation(tag = "AYAH", annotation = index.toString())
            if (isActive) {
                pushStyle(
                    SpanStyle(
                        background = if (isDark) Color(0xFF2DD4BF).copy(alpha = 0.22f) else Color(0xFFC86D51).copy(alpha = 0.14f),
                        color = if (isDark) Color.White else Color(0xFF211E1C),
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
            append(ayahText)
            if (isActive) pop()
            pop() // pop string annotation

            append(' ')
            appendInlineContent("marker_${ayah.numberInSurah}", "[${ayah.numberInSurah}]")
            append(' ')
        }
    }
}

internal fun String.withoutProviderBismillahPrefix(): String {
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
