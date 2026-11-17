package com.zaidan.quraneasy.feature.quran.presentation

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import my.nanihadesuka.compose.ScrollbarSelectionActionable
import my.nanihadesuka.compose.ScrollbarSelectionMode
import my.nanihadesuka.compose.ScrollbarSettings

@Composable
fun quranScrollbarSettings() = ScrollbarSettings.Default.copy(
    alwaysShowScrollbar = false,
    thumbThickness = 8.dp,
    thumbUnselectedColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
    thumbSelectedColor = MaterialTheme.colorScheme.primary,
    scrollbarPadding = 6.dp,
    selectionMode = ScrollbarSelectionMode.Thumb,
    selectionActionable = ScrollbarSelectionActionable.Always
)
