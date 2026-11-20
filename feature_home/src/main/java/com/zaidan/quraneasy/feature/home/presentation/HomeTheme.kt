package com.zaidan.quraneasy.feature.home.presentation

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

/** Home's supplied references have their own palette, independent of the reader. */
@Composable
internal fun HomeTheme(content: @Composable () -> Unit) {
    val base = MaterialTheme.colorScheme
    val dark = base.background.luminance() < 0.2f
    val colors = if (dark) base.copy(
        background = Color(0xFF0F1411), surface = Color(0xFF0A0F0D),
        surfaceContainerLowest = Color(0xFF0A0F0D), surfaceContainerLow = Color(0xFF111815),
        surfaceContainer = Color(0xFF16201B), onSurface = Color.White,
        onBackground = Color(0xFFF0FDF4), onSurfaceVariant = Color(0xFF94A3B8),
        primary = Color(0xFF10B981), onPrimary = Color.Black,
        primaryContainer = Color(0xFF16201B), secondary = Color(0xFF06B6D4),
        onSecondary = Color.Black, secondaryContainer = Color(0xFF0E2428),
        tertiary = Color(0xFF38BDF8), tertiaryContainer = Color(0xFF10252E),
        outlineVariant = Color(0xFF10B981).copy(alpha = 0.2f),
    ) else base.copy(
        background = Color(0xFFFAF8F5), surface = Color.White,
        surfaceContainerLowest = Color.White, surfaceContainerLow = Color(0xFFF5F0E6),
        surfaceContainer = Color(0xFFEBE5D8), onSurface = Color(0xFF211E1C),
        onBackground = Color(0xFF211E1C), onSurfaceVariant = Color(0xFF6F7E74),
        primary = Color(0xFF35483C), onPrimary = Color.White,
        primaryContainer = Color(0xFFE8EFEA), secondary = Color(0xFFC86D51),
        onSecondary = Color.White, secondaryContainer = Color(0xFFF9E5DF),
        tertiary = Color(0xFFD4A359), tertiaryContainer = Color(0xFFFBF3DF),
        outlineVariant = Color(0xFFE8DFC9),
    )
    MaterialTheme(colorScheme = colors, content = content)
}
