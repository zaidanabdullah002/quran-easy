package com.zaidan.quraneasy.feature.home.presentation

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

internal data class SanctuaryPalette(
    val Background: Color,
    val Surface: Color,
    val ElevatedSurface: Color,
    val Outline: Color,
    val Text: Color,
    val MutedText: Color,
    val Forest: Color,
    val ForestSoft: Color,
    val Terracotta: Color,
    val TerracottaSoft: Color,
    val Gold: Color,
    val GoldSoft: Color,
)

/** Home colors follow the app theme so every Sanctuary component has real light/dark parity. */
internal val SanctuaryColors: SanctuaryPalette
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.let { colors ->
        SanctuaryPalette(
            Background = colors.background,
            Surface = colors.surfaceContainerLowest,
            ElevatedSurface = colors.surfaceContainerLow,
            Outline = colors.outlineVariant,
            Text = colors.onSurface,
            MutedText = colors.onSurfaceVariant,
            Forest = colors.primary,
            ForestSoft = colors.primaryContainer,
            Terracotta = colors.secondary,
            TerracottaSoft = colors.secondaryContainer,
            Gold = colors.tertiary,
            GoldSoft = colors.tertiaryContainer,
        )
    }
