package com.zaidan.quraneasy.core.ui

import androidx.compose.animation.core.animateRectAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TourOverlay(
    coordinator: TourCoordinator,
    modifier: Modifier = Modifier,
    onExitTour: () -> Unit,
) {
    val step = coordinator.currentStep ?: return
    val targetBounds = coordinator.getTargetBounds(step.targetId) ?: Rect.Zero
    
    val animatedBounds by animateRectAsState(
        targetValue = targetBounds,
        animationSpec = tween(durationMillis = 600),
        label = "SpotlightBounds"
    )

    val density = LocalDensity.current

    Box(modifier = modifier.fillMaxSize()) {
        // Scrim with spotlight hole using BlendMode.Clear
        // graphicsLayer with Offscreen compositing is essential for BlendMode.Clear to work
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
        ) {
            val padding = with(density) { 10.dp.toPx() }
            val radius = with(density) { 16.dp.toPx() }

            // 1. Draw the dimmed background (this covers the whole screen)
            drawRect(color = Color.Black.copy(alpha = 0.7f))
            
            if (animatedBounds != Rect.Zero && animatedBounds.height > 0) {
                // 2. Clear the hole (this makes the target area 100% transparent)
                drawRoundRect(
                    color = Color.Transparent,
                    topLeft = Offset(animatedBounds.left - padding, animatedBounds.top - padding),
                    size = Size(animatedBounds.width + padding * 2, animatedBounds.height + padding * 2),
                    cornerRadius = CornerRadius(radius, radius),
                    blendMode = BlendMode.Clear
                )
                
                // 3. Draw a glowing border around the hole to focus it
                drawRoundRect(
                    color = Color.White.copy(alpha = 0.4f),
                    topLeft = Offset(animatedBounds.left - padding, animatedBounds.top - padding),
                    size = Size(animatedBounds.width + padding * 2, animatedBounds.height + padding * 2),
                    cornerRadius = CornerRadius(radius, radius),
                    style = Stroke(width = with(density) { 2.dp.toPx() })
                )
            }
        }

        // Tooltip
        if (animatedBounds != Rect.Zero && animatedBounds.height > 0) {
            // Determine if the tooltip should be above or below the target
            val screenHeight = 2000f // Fallback large value
            val isTooltipBelow = animatedBounds.top < 400.dp.value * density.density
            
            val tooltipY = if (isTooltipBelow) {
                animatedBounds.bottom + with(density) { 24.dp.toPx() }
            } else {
                animatedBounds.top - with(density) { 180.dp.toPx() } // Approximate height
            }
            
            Column(
                modifier = Modifier
                    .offset { IntOffset(0, tooltipY.toInt()) }
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(24.dp),
                    tonalElevation = 8.dp,
                    shadowElevation = 12.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = step.text,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(onClick = onExitTour) {
                                Text("End Tour", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                            }
                            
                            // Step Progress Indicator
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = RoundedCornerShape(50.dp)
                            ) {
                                Text(
                                    text = "${TourStep.entries.indexOf(step) + 1} / ${TourStep.entries.size}",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun Modifier.tourTarget(
    coordinator: TourCoordinator,
    id: String
): Modifier = this.onGloballyPositioned { coordinates ->
    if (coordinates.isAttached) {
        coordinator.registerTarget(id, coordinates)
    }
}
