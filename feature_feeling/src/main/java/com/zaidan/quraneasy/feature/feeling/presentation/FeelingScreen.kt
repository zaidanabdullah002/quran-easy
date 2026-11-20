package com.zaidan.quraneasy.feature.feeling.presentation

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaidan.quraneasy.feature.quran.presentation.audio.ReaderAudioUiState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zaidan.quraneasy.core.theme.LocalQuranTypography
import com.zaidan.quraneasy.core.settings.ReaderSettingsEffects

@Composable
fun FeelingDetailScreen(viewModel: FeelingDetailViewModel = hiltViewModel(), onBackClick: () -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val audioState by viewModel.audioState.collectAsStateWithLifecycle()
    DisposableEffect(viewModel) { onDispose(viewModel::stopAudio) }
    ReaderSettingsEffects()
    FeelingDetailScreen(uiState, onBackClick, audioState, viewModel::listen, viewModel::loadFeeling)
}

@Composable
fun FeelingDetailScreen(uiState: FeelingDetailUiState, onBackClick: () -> Unit,
    audioState: ReaderAudioUiState = ReaderAudioUiState(), onListen: (Int) -> Unit = {},
    onRetry: () -> Unit = {}) {
    val base = MaterialTheme.colorScheme
    val colors = if (base.background.luminance() < .2f) base.copy(
        background = Color(0xFF080C0E), surfaceContainerLowest = Color(0xFF0F171D),
        surfaceContainerLow = Color(0xFF080C0E), primary = Color(0xFF38BDF8),
        secondary = Color(0xFF7DD3FC), onSurface = Color(0xFFE2E8F0),
        onSurfaceVariant = Color(0xFF94A3B8), outlineVariant = Color(0xFF06B6D4).copy(alpha = .2f)
    ) else base
    MaterialTheme(colorScheme = colors) {
    val feeling = uiState.feeling
    Column(Modifier.fillMaxSize().background(colors.background).statusBarsPadding()) {
        Row(Modifier.fillMaxWidth().height(56.dp).padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
            Text("Feeling Detail", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        }
        LazyColumn(Modifier.fillMaxSize().navigationBarsPadding(), contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item {
                Surface(color = colors.surfaceContainerLowest, border = BorderStroke(1.dp, colors.outlineVariant), shape = RoundedCornerShape(12.dp)) {
                    Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("SPIRITUAL REFLECTIONS", color = colors.secondary, style = MaterialTheme.typography.labelLarge)
                        Text("${feeling?.emoji.orEmpty()}  ${feeling?.title ?: "A moment of reflection"}",
                            fontSize = 26.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
                        Text(feeling?.subtitle ?: "A calm space for the heart",
                            color = colors.secondary, fontSize = 18.sp, lineHeight = 26.sp, fontStyle = FontStyle.Italic)
                    }
                }
            }
            when {
                uiState.isLoading -> item {
                    Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                }
                uiState.message != null -> item {
                    Text(uiState.message, color = colors.error, modifier = Modifier.padding(16.dp))
                    TextButton(onClick = onRetry) { Text("Try again") }
                }
                else -> itemsIndexed(uiState.verses, key = { _, v -> "${v.surahNumber}:${v.numberInSurah}" }) { index, verse ->
                    FeelingVerseCard(index + 1, verse, feeling, audioState, onListen = { onListen(index) })
                }
            }
        }
    }
    }
}

@Composable
private fun FeelingVerseCard(number: Int, verse: FeelingVerseUiModel, feeling: Feeling?,
    audioState: ReaderAudioUiState, onListen: () -> Unit) {
    var reflectionExpanded by rememberSaveable(feeling?.id, verse.surahNumber, verse.numberInSurah) { mutableStateOf(false) }
    val colors = MaterialTheme.colorScheme
    val typography = LocalQuranTypography.current
    val context = LocalContext.current
    Surface(color = colors.surfaceContainerLowest, shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, colors.outlineVariant)) {
        Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(color = colors.primary.copy(alpha = .15f), shape = CircleShape) {
                    Text(number.toString(), Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        color = colors.primary, fontWeight = FontWeight.Bold)
                }
                Text("Surah ${verse.surahNumber} · Verse ${verse.numberInSurah}",
                    Modifier.weight(1f).padding(start = 10.dp), style = MaterialTheme.typography.labelLarge)
                IconButton(onClick = {
                    context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, "${verse.arabicText}\n\n${verse.translation.orEmpty()}\nQuran ${verse.surahNumber}:${verse.numberInSurah}")
                    }, "Share verse"))
                }) { Icon(Icons.Default.Share, "Share verse", tint = colors.onSurfaceVariant) }
            }
            SelectionContainer {
                Text(verse.arabicText, Modifier.fillMaxWidth(), style = TextStyle(textDirection = TextDirection.Rtl), fontFamily = typography.fontFamily,
                    fontSize = typography.ayahFontSize, lineHeight = typography.ayahLineHeight,
                    textAlign = TextAlign.End, color = colors.onSurface)
            }
            verse.translation?.takeIf { it.isNotBlank() }?.let {
                Surface(color = colors.surfaceContainerLow, shape = RoundedCornerShape(12.dp)) {
                    SelectionContainer {
                        Text(it, Modifier.fillMaxWidth().padding(16.dp), fontSize = 14.sp, lineHeight = 24.sp,
                            fontStyle = FontStyle.Italic, color = colors.onSurface)
                    }
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                val active = audioState.currentIndex == number - 1
                OutlinedButton(onClick = onListen) {
                    Text(when {
                        active && audioState.isPreparing -> "Cancel loading"
                        active && audioState.isPlaying -> "Pause"
                        active && audioState.errorMessage != null -> "Retry audio"
                        else -> "Listen"
                    })
                }
                TextButton(onClick = { reflectionExpanded = !reflectionExpanded }) {
                    Text(if (reflectionExpanded) "Hide reflection" else "Reflection")
                }
            }
            if (audioState.currentIndex == number - 1) audioState.errorMessage?.let {
                Text(it, color = colors.error, style = MaterialTheme.typography.bodySmall)
            }
            AnimatedVisibility(reflectionExpanded) {
                Surface(color = colors.surfaceContainerLow, shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, colors.outlineVariant)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("HEART REFLECTION", color = colors.primary, style = MaterialTheme.typography.labelMedium)
                        Text(feeling?.subtitle.orEmpty(), fontWeight = FontWeight.SemiBold)
                        Text("Read this verse slowly. What stands out to you, and what small action could you take today?",
                            style = MaterialTheme.typography.bodyMedium)
                        Text("Personal reflection prompt · Not tafsir", style = MaterialTheme.typography.labelSmall,
                            color = colors.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
