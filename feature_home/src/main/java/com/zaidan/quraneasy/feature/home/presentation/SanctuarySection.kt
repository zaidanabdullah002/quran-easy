package com.zaidan.quraneasy.feature.home.presentation

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.ui.platform.LocalContext
import com.zaidan.quraneasy.core.theme.LocalQuranTypography
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.zaidan.quraneasy.feature.feeling.presentation.FeelingVerseUiModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaidan.quraneasy.core.ui.QuranEasySurface
import com.zaidan.quraneasy.feature.feeling.presentation.Feeling
import com.zaidan.quraneasy.feature.feeling.presentation.VerseRef

private const val HopeVerseTranslation =
    "Say: O My servants who have transgressed against themselves! Despair not of the mercy of Allah, indeed Allah forgives all sins. Truly He is Forgiving, Most Merciful."

@Composable
fun SanctuarySection(
    feelings: List<Feeling>,
    onFeelingClick: (Feeling) -> Unit,
    spotlights: Map<String, FeelingVerseUiModel> = emptyMap(),
    onLoadSpotlight: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val moods = remember(feelings) { orderedMoods(feelings) }
    var selectedMoodId by rememberSaveable { mutableStateOf<String?>(null) }
    var showAllMoods by rememberSaveable { mutableStateOf(false) }
    val selectedMood = moods.firstOrNull { it.id == selectedMoodId } ?: moods.first()
    LaunchedEffect(selectedMood.id) { onLoadSpotlight(selectedMood.id) }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                Text(
                    text = "SPIRITUAL SANCTUARY",
                    color = SanctuaryColors.Terracotta,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.1.sp
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "How is your heart feeling today?",
                    color = SanctuaryColors.Text,
                    fontSize = 18.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "All Moods",
                modifier = Modifier.clickable { showAllMoods = true }.padding(8.dp),
                color = SanctuaryColors.Terracotta,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(end = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            userScrollEnabled = true
        ) {
            items(moods, key = { it.id }) { mood ->
                MoodChip(
                    label = moodLabel(mood),
                    icon = moodIcon(mood.id),
                    selected = mood.id == selectedMood.id,
                    onClick = { selectedMoodId = mood.id }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            SanctuaryWatermark(modifier = Modifier.fillMaxSize())
            VerseSpotlightCard(
                feeling = selectedMood,
                resolvedVerse = spotlights[selectedMood.id],
                onExplore = { onFeelingClick(selectedMood) }
            )
        }
    }
    if (showAllMoods) AlertDialog(
        onDismissRequest = { showAllMoods = false },
        title = { Text("How are you feeling?") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                feelings.forEach { feeling ->
                    TextButton(onClick = { showAllMoods = false; onFeelingClick(feeling) }) {
                        Text("${feeling.emoji}  ${feeling.title}")
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = { showAllMoods = false }) { Text("Close") } },
    )
}

@Composable
private fun MoodChip(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = if (selected) SanctuaryColors.Terracotta else SanctuaryColors.ElevatedSurface,
        contentColor = if (selected) SanctuaryColors.Surface else SanctuaryColors.Text,
        border = BorderStroke(
            1.dp,
            if (selected) SanctuaryColors.Terracotta else SanctuaryColors.Outline
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
            horizontalArrangement = Arrangement.spacedBy(7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(15.dp))
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun VerseSpotlightCard(
    feeling: Feeling,
    resolvedVerse: FeelingVerseUiModel?,
    onExplore: () -> Unit
) {
    val verse = feeling.verses.firstOrNull() ?: VerseRef(39, 53)
    val context = LocalContext.current
    val typography = LocalQuranTypography.current
    QuranEasySurface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, SanctuaryColors.Outline),
        elevation = 1.dp,
        containerColor = SanctuaryColors.ElevatedSurface.copy(alpha = 0.96f),
        contentColor = SanctuaryColors.Text,
        contentPadding = PaddingValues(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SanctuaryColors.TerracottaSoft
            ) {
                Text(
                    text = feeling.subtitle.uppercase(),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                    color = SanctuaryColors.Terracotta,
                    fontSize = 8.sp,
                    lineHeight = 10.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                VerseMeta(label = "Ayah", value = verse.ayah.toString())
                VerseMeta(label = "Surah", value = verse.surah.toString())
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = resolvedVerse?.arabicText ?: "Open Explore Verses to read this reflection",
            fontFamily = if (resolvedVerse != null) typography.fontFamily else null,
            modifier = Modifier.fillMaxWidth(),
            color = SanctuaryColors.Text,
            fontSize = 21.sp,
            lineHeight = 34.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.End
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.Top) {
            Surface(
                modifier = Modifier
                    .size(width = 2.dp, height = 86.dp),
                color = SanctuaryColors.Terracotta,
                shape = CircleShape
            ) {}
            Text(
                text = (resolvedVerse?.translation ?: feeling.previewTranslation)?.let { "“$it”" } ?: feeling.subtitle,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp),
                color = SanctuaryColors.Text.copy(alpha = 0.82f),
                fontSize = 12.sp,
                lineHeight = 18.sp,
                fontStyle = FontStyle.Italic,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = SanctuaryColors.Outline)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                VerseActionIcon(Icons.Outlined.VolumeUp, "Listen unavailable")
                VerseActionIcon(Icons.Outlined.BookmarkBorder, "Verse bookmark unavailable")
                VerseActionIcon(Icons.Outlined.Share, "Share", enabled = resolvedVerse != null) {
                    resolvedVerse?.let { resolved ->
                        context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, "${resolved.arabicText}\n\n${resolved.translation.orEmpty()}\nQuran ${resolved.surahNumber}:${resolved.numberInSurah}")
                        }, "Share verse"))
                    }
                }
            }
            Surface(
                onClick = onExplore,
                shape = CircleShape,
                color = SanctuaryColors.Terracotta,
                contentColor = SanctuaryColors.Surface
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Explore Verses", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun VerseMeta(label: String, value: String) {
    Column {
        Text(label, color = SanctuaryColors.MutedText, fontSize = 8.sp)
        Text(value, color = SanctuaryColors.MutedText, fontSize = 9.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun VerseActionIcon(icon: ImageVector, description: String, enabled: Boolean = false, onClick: () -> Unit = {}) {
    IconButton(onClick = onClick, enabled = enabled, modifier = Modifier.size(40.dp)) {
        Icon(
            icon,
            contentDescription = description,
            tint = SanctuaryColors.MutedText.copy(alpha = if (enabled) 1f else 0.4f),
            modifier = Modifier.size(17.dp)
        )
    }
}

@Composable
private fun SanctuaryWatermark(modifier: Modifier = Modifier) {
    val lineColor = SanctuaryColors.Gold.copy(alpha = 0.08f)
    Canvas(modifier = modifier) {
        val side = size.minDimension * 0.72f
        val topLeft = Offset((size.width - side) / 2f, (size.height - side) / 2f)
        drawCircle(
            color = lineColor,
            radius = side * 0.46f,
            center = center,
            style = Stroke(width = 1.2.dp.toPx())
        )
        drawRect(
            color = lineColor,
            topLeft = topLeft,
            size = Size(side, side),
            style = Stroke(width = 1.dp.toPx())
        )
        rotate(45f, pivot = center) {
            drawRect(
                color = lineColor,
                topLeft = topLeft,
                size = Size(side, side),
                style = Stroke(width = 1.dp.toPx())
            )
        }
    }
}

private fun orderedMoods(feelings: List<Feeling>): List<Feeling> {
    val preferredIds = listOf("hope", "peace", "gratitude")
    val preferred = preferredIds
        .mapNotNull { id -> feelings.firstOrNull { it.id == id } }
    if (feelings.isNotEmpty()) {
        return preferred + feelings.filterNot { feeling -> feeling.id in preferredIds }
    }
    return listOf(
            Feeling(
                id = "hope",
                emoji = "☀",
                title = "Need Hope",
                subtitle = "Allah's mercy never ends",
                verses = listOf(VerseRef(39, 53)),
                previewTranslation = HopeVerseTranslation,
                accent = "#C87351"
            ),
            Feeling(
                id = "peace",
                emoji = "❧",
                title = "Need Peace",
                subtitle = "Let the heart become still",
                verses = listOf(VerseRef(13, 28)),
                accent = "#294638"
            ),
            Feeling(
                id = "gratitude",
                emoji = "✦",
                title = "Need Gratitude",
                subtitle = "Notice the blessings around you",
                verses = listOf(VerseRef(14, 7)),
                accent = "#AA8A43"
            )
        )
}

private fun moodLabel(feeling: Feeling): String = when (feeling.id) {
    "peace" -> "Seeking Peace"
    "gratitude" -> "Gratitude"
    else -> feeling.title
}

private fun moodIcon(id: String): ImageVector = when (id) {
    "peace" -> Icons.Outlined.Spa
    "gratitude" -> Icons.Outlined.AutoAwesome
    else -> Icons.Outlined.LightMode
}
