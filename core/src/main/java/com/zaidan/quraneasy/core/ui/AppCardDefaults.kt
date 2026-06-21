package com.zaidan.quraneasy.core.ui

import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

object AppCardDefaults {
    @Composable
    fun interactiveElevation(
        defaultElevation: androidx.compose.ui.unit.Dp = 3.dp,
        pressedElevation: androidx.compose.ui.unit.Dp = 0.dp
    ): CardElevation = CardDefaults.cardElevation(
        defaultElevation = defaultElevation,
        pressedElevation = pressedElevation
    )

    @Composable
    fun staticElevation(
        defaultElevation: androidx.compose.ui.unit.Dp = 2.dp
    ): CardElevation = CardDefaults.cardElevation(
        defaultElevation = defaultElevation
    )
}
