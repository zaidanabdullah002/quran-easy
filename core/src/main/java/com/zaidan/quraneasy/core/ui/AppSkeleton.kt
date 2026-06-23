package com.zaidan.quraneasy.core.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.zaidan.quraneasy.core.theme.AppSoftSurface
import com.zaidan.quraneasy.core.theme.AppSurface

@Composable
fun AppSkeletonBlock(
    modifier: Modifier = Modifier,
    tint: Color = AppSoftSurface
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .skeletonBrush(tint)
    )
}

@Composable
fun AppSkeletonLine(
    modifier: Modifier = Modifier,
    tint: Color = AppSoftSurface
) {
    AppSkeletonBlock(
        modifier = modifier
            .fillMaxWidth()
            .height(14.dp),
        tint = tint
    )
}

private fun Modifier.skeletonBrush(
    baseColor: Color
): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "skeleton")
    val shift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "skeleton_shift"
    )
    background(
        brush = Brush.linearGradient(
            colors = listOf(
                baseColor.copy(alpha = 0.92f),
                AppSurface,
                baseColor.copy(alpha = 0.92f)
            ),
            start = Offset(shift - 280f, shift - 280f),
            end = Offset(shift, shift)
        )
    )
}
