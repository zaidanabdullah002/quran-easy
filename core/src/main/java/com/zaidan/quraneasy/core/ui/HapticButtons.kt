package com.zaidan.quraneasy.core.ui

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun HapticIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    type: AppHapticType = AppHapticType.Tap,
    content: @Composable () -> Unit
) {
    IconButton(
        onClick = hapticClick(type = type, onClick = onClick),
        modifier = modifier,
        enabled = enabled,
        content = content
    )
}

@Composable
fun HapticTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    type: AppHapticType = AppHapticType.Tap,
    content: @Composable RowScope.() -> Unit
) {
    TextButton(
        onClick = hapticClick(type = type, onClick = onClick),
        modifier = modifier,
        enabled = enabled,
        content = content
    )
}
