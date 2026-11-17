package com.zaidan.quraneasy.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zaidan.quraneasy.core.theme.AppDimens
import androidx.compose.foundation.shape.RoundedCornerShape

/**
 * Shared surface for the app's content panels.
 *
 * Every screen should use this instead of recreating its own card colors, border,
 * radius, and elevation. The values follow [MaterialTheme], so a later theme pass
 * does not require touching every feature module.
 */
@Composable
fun QuranEasySurface(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(AppDimens.ScreenPadding.dp),
    shape: Shape = RoundedCornerShape(AppDimens.CardRadius.dp),
    border: BorderStroke? = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    elevation: Dp = 0.dp,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier,
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor,
        ),
        border = border,
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
    ) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.padding(contentPadding),
            content = content,
        )
    }
}

/**
 * Clickable variant of [QuranEasySurface] with the app's standard tap feedback.
 */
@Composable
fun QuranEasyActionSurface(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(AppDimens.ScreenPadding.dp),
    shape: Shape = RoundedCornerShape(AppDimens.CardRadius.dp),
    border: BorderStroke? = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    elevation: Dp = 0.dp,
    enabled: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    content: @Composable ColumnScope.() -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Card(
        onClick = hapticClick(onClick = onClick),
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        border = border,
        elevation = AppCardDefaults.interactiveElevation(defaultElevation = elevation),
        interactionSource = interactionSource,
    ) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.padding(contentPadding),
            content = content,
        )
    }
}
