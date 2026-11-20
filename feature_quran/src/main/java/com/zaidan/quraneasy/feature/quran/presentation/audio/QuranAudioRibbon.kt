package com.zaidan.quraneasy.feature.quran.presentation.audio

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaidan.quraneasy.core.ui.HapticIconButton

@Composable
fun QuranAudioRibbon(
    titleLabel: String,
    positionLabel: String,
    totalAyahs: Int,
    audioUiState: ReaderAudioUiState,
    onToggleAudio: () -> Unit,
    onPreviousAyah: () -> Unit,
    onNextAyah: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val darkTheme = colors.background.luminance() < 0.2f
    val containerBg = if (darkTheme) Color(0xFF0C1310).copy(alpha = 0.96f) else Color(0xFFFAF8F5).copy(alpha = 0.96f)
    val borderColor = if (darkTheme) Color(0xFF10B981).copy(alpha = 0.3f) else Color(0xFFE8DFC9)
    val accentTeal = Color(0xFF2DD4BF)
    val accentSecondary = if (darkTheme) accentTeal else Color(0xFFC86D51)
    val totalCount = totalAyahs.coerceAtLeast(1)
    val progressFraction = ((audioUiState.currentIndex + 1).toFloat() / totalCount).coerceIn(0f, 1f)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(18.dp),
                ambientColor = if (darkTheme) Color.Black else Color(0x2035483C),
                spotColor = if (darkTheme) Color(0x3310B981) else Color(0x2035483C)
            ),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = containerBg),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // 1. Progress Bar across top of the ribbon card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.5.dp)
                    .background(if (darkTheme) Color(0xFF16221C) else Color(0xFFEBE5D8))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progressFraction)
                        .height(3.5.dp)
                        .background(
                            if (darkTheme) {
                                Brush.horizontalGradient(listOf(Color(0xFF06B6D4), Color(0xFF10B981)))
                            } else {
                                Brush.horizontalGradient(listOf(Color(0xFFC86D51), Color(0xFFB4583C)))
                            }
                        )
                )
            }

            // 2. Main Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Equalizer box
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (darkTheme) Color(0xFF111C17) else Color(0xFFF9E5DF))
                        .border(
                            1.dp,
                            if (darkTheme) Color(0xFF2DD4BF).copy(alpha = 0.3f) else Color(0xFFC86D51).copy(alpha = 0.2f),
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.GraphicEq,
                        contentDescription = null,
                        tint = if (darkTheme) Color(0xFF2DD4BF) else Color(0xFFC86D51),
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.size(10.dp))

                // Track Info
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Mishary Rashid Alafasy",
                            color = if (darkTheme) Color.White else Color(0xFF211E1C),
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        Spacer(modifier = Modifier.size(6.dp))
                        Text(
                            text = "HQ",
                            color = if (darkTheme) Color(0xFF6EE7B7) else Color(0xFF8C5E43),
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (darkTheme) Color(0xFF10B981).copy(alpha = 0.2f) else Color(0xFFF5F0E6))
                                .border(
                                    0.8.dp,
                                    if (darkTheme) Color(0xFF10B981).copy(alpha = 0.3f) else Color(0xFFE8DFC9),
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    if (audioUiState.errorMessage != null) {
                        Text(
                            text = audioUiState.errorMessage,
                            color = colors.error,
                            fontSize = 11.5.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "$titleLabel · ",
                                color = if (darkTheme) Color(0xFF94A3B8) else Color(0xFF7C8B80),
                                fontSize = 11.5.sp,
                                maxLines = 1
                            )
                            Text(
                                text = positionLabel,
                                color = accentSecondary,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                    }
                }

                // Controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    HapticIconButton(
                        onClick = onPreviousAyah,
                        enabled = audioUiState.currentIndex > 0,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.SkipPrevious,
                            contentDescription = "Previous ayah",
                            tint = if (darkTheme) {
                                Color(0xFF94A3B8).copy(alpha = if (audioUiState.currentIndex > 0) 1f else 0.35f)
                            } else {
                                Color(0xFF7A8B7F).copy(alpha = if (audioUiState.currentIndex > 0) 1f else 0.35f)
                            },
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    HapticIconButton(
                        onClick = onToggleAudio,
                        enabled = totalAyahs > 0,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .then(
                                if (darkTheme) {
                                    Modifier.background(
                                        Brush.linearGradient(
                                            listOf(Color(0xFF06B6D4), Color(0xFF2DD4BF), Color(0xFF10B981))
                                        )
                                    )
                                } else {
                                    Modifier.background(
                                        Brush.linearGradient(
                                            listOf(Color(0xFFC86D51), Color(0xFFB4583C))
                                        )
                                    )
                                }
                            )
                    ) {
                        if (audioUiState.isPreparing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = if (darkTheme) Color.Black else Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = if (audioUiState.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                contentDescription = if (audioUiState.isPlaying) "Pause recitation" else "Play recitation",
                                tint = if (darkTheme) Color.Black else Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    HapticIconButton(
                        onClick = onNextAyah,
                        enabled = audioUiState.currentIndex < (totalAyahs - 1),
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.SkipNext,
                            contentDescription = "Next ayah",
                            tint = if (darkTheme) {
                                Color(0xFF94A3B8).copy(alpha = if (audioUiState.currentIndex < (totalAyahs - 1)) 1f else 0.35f)
                            } else {
                                Color(0xFF7A8B7F).copy(alpha = if (audioUiState.currentIndex < (totalAyahs - 1)) 1f else 0.35f)
                            },
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

