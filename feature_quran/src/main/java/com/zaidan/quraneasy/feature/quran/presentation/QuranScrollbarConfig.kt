package com.zaidan.quraneasy.feature.quran.presentation

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import my.nanihadesuka.compose.ScrollbarSelectionActionable
import my.nanihadesuka.compose.ScrollbarSelectionMode
import my.nanihadesuka.compose.ScrollbarSettings

fun quranScrollbarSettings() = ScrollbarSettings.Default.copy(
    alwaysShowScrollbar = false,
    thumbThickness = 8.dp,
    thumbUnselectedColor = Color(0x664A5875),
    thumbSelectedColor = Color(0xFF4A5875),
    scrollbarPadding = 6.dp,
    selectionMode = ScrollbarSelectionMode.Thumb,
    selectionActionable = ScrollbarSelectionActionable.Always
)
