package com.zaidan.quraneasy.feature.prayer.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaidan.quraneasy.core.ui.QuranEasySurface
import com.zaidan.quraneasy.core.ui.hapticClickable
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun CompactPrayerTrackerCard(
    uiState: PrayerCardUiState,
    onTogglePrayer: (Int, Boolean) -> Unit,
    onCardClick: (() -> Unit)? = null
) {
    val colors = MaterialTheme.colorScheme
    val ready = uiState.cardMode == PrayerCardMode.Ready ||
        uiState.cardMode == PrayerCardMode.ReadyOfflineCache
    val prayers = if (ready) uiState.prayers else PrayerRowUi.placeholderList()
    val trackablePrayers = prayers.filter { it.trackable }
    val completedCount = trackablePrayers.count { it.completed }
    val completionProgress = if (trackablePrayers.isEmpty()) 0f else {
        completedCount / trackablePrayers.size.toFloat()
    }
    val currentPrayer = rememberPrayerProgress(prayers)
    val animatedCompletion = rememberSweepingProgress(
        target = completionProgress,
        durationMillis = 520
    )
    val statusText = when (uiState.cardMode) {
        PrayerCardMode.Ready, PrayerCardMode.ReadyOfflineCache ->
            "$completedCount of ${trackablePrayers.size} completed"
        PrayerCardMode.Loading -> "Loading prayer times"
        PrayerCardMode.NoPermission -> "Location permission required"
        PrayerCardMode.Unavailable -> "Prayer times unavailable"
    }
    val location = uiState.locationLabel ?: when (uiState.cardMode) {
        PrayerCardMode.NoPermission -> "Enable location for local prayer times"
        PrayerCardMode.Loading -> "Finding your location"
        else -> "Prayer times"
    }

    QuranEasySurface(
        modifier = Modifier
            .fillMaxWidth()
            .hapticClickable(
                enabled = onCardClick != null,
                onClick = { onCardClick?.invoke() }
            ),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, colors.outlineVariant),
        elevation = 1.dp,
        containerColor = colors.surfaceContainerLowest,
        contentColor = colors.onSurface,
        contentPadding = PaddingValues(16.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.LocationOn, null, tint = colors.onSurfaceVariant, modifier = Modifier.size(14.dp))
            Text(location, modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
                color = colors.onSurfaceVariant, fontSize = 10.sp, maxLines = 2)
            Text(statusText, color = colors.primary, fontSize = 10.sp,
                modifier = Modifier.clip(CircleShape).background(colors.primaryContainer).padding(8.dp, 4.dp))
        }
        Spacer(Modifier.height(12.dp))
        SolarPrayerPanel(prayers, currentPrayer, colors)
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            trackablePrayers.forEach { prayer ->
                CompactPrayerItem(prayer, prayer.name == currentPrayer?.name,
                    ready && prayer.enabled, colors) {
                    onTogglePrayer(prayers.indexOf(prayer), prayer.completed)
                }
            }
        }
    }
}

@Composable
private fun SolarPrayerPanel(
    prayers: List<PrayerRowUi>,
    current: CurrentPrayerProgress?,
    colors: androidx.compose.material3.ColorScheme,
) {
    // Use the actual schedule; the reference's Mountain View times are mock data.
    val sunrise = prayers.firstOrNull { it.name == "Sunrise" }?.time ?: "--:--"
    val fajr = prayers.firstOrNull { it.name == "Fajr" }?.time ?: "--:--"
    val sunset = prayers.firstOrNull { it.name == "Maghrib" }?.time ?: "--:--"
    fun minutes(value: String): Int? = value.split(":").takeIf { it.size == 2 }?.let {
        val h = it[0].toIntOrNull() ?: return@let null
        val m = it[1].toIntOrNull() ?: return@let null
        if (h in 0..23 && m in 0..59) h * 60 + m else null
    }
    val dawn = minutes(fajr)
    val dusk = minutes(sunset)
    val rise = minutes(sunrise)
    val lastThird = if (dawn != null && dusk != null) {
        val night = (dawn - dusk + 1440) % 1440
        val start = (dusk + night * 2 / 3) % 1440
        "%02d:%02d – %s".format(start / 60, start % 60, fajr)
    } else "--:--"
    val now = java.util.Calendar.getInstance().let {
        it.get(java.util.Calendar.HOUR_OF_DAY) * 60 + it.get(java.util.Calendar.MINUTE)
    }
    val daylight = rise != null && dusk != null && dusk > rise && now in rise..dusk
    val position = if (daylight) ((now - rise!!).toFloat() / (dusk!! - rise)).coerceIn(0f, 1f) else 0.5f
    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
        .background(colors.surfaceContainerLow).border(1.dp, colors.outlineVariant, RoundedCornerShape(16.dp)).padding(12.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Last third of night\n$lastThird", color = colors.onSurfaceVariant, fontSize = 10.sp, lineHeight = 14.sp)
            Text("Sunrise\n$sunrise", color = colors.onSurfaceVariant, fontSize = 10.sp, lineHeight = 14.sp, textAlign = TextAlign.End)
        }
        androidx.compose.foundation.Canvas(Modifier.fillMaxWidth().height(56.dp).padding(vertical = 4.dp)) {
            val left = 5.dp.toPx()
            val width = size.width - left * 2
            val base = size.height * 0.9f
            val curve = androidx.compose.ui.graphics.Path().apply {
                moveTo(left, base)
                quadraticBezierTo(size.width / 2, -size.height * 0.8f, size.width - left, base)
            }
            drawLine(colors.outlineVariant, androidx.compose.ui.geometry.Offset(left, base),
                androidx.compose.ui.geometry.Offset(size.width - left, base), strokeWidth = 1.dp.toPx(),
                pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(6f, 6f)))
            drawPath(curve, Brush.horizontalGradient(listOf(colors.primary, colors.tertiary, colors.secondary)),
                style = androidx.compose.ui.graphics.drawscope.Stroke(2.5.dp.toPx()))
            if (current != null) {
                val x = left + width * position
                val y = base - 3.4f * size.height * position * (1 - position)
                val center = androidx.compose.ui.geometry.Offset(x, y)
                drawCircle(colors.secondary.copy(alpha = 0.15f), 10.dp.toPx(), center)
                drawCircle(colors.secondary, 5.dp.toPx(), center)
                if (!daylight) drawCircle(colors.surfaceContainerLow, 4.dp.toPx(), center + androidx.compose.ui.geometry.Offset(3.dp.toPx(), -2.dp.toPx()))
            }
        }
        HorizontalDivider(color = colors.outlineVariant)
        Row(Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(current?.let { "Next: ${it.name}" } ?: "Waiting for prayer times",
                modifier = Modifier.weight(1f), color = colors.onSurface, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            current?.let {
                Text("${it.timeLeftLabel} left", color = colors.secondary, fontSize = 11.sp,
                    modifier = Modifier.clip(CircleShape).background(colors.secondaryContainer).padding(8.dp, 4.dp))
            }
        }
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.CompactPrayerItem(
    prayer: PrayerRowUi,
    isCurrent: Boolean,
    enabled: Boolean,
    colors: androidx.compose.material3.ColorScheme,
    onClick: () -> Unit
) {
    val targetAccent = when {
        prayer.completed -> colors.primary
        isCurrent -> colors.secondary
        else -> colors.onSurfaceVariant
    }
    val accent by animateColorAsState(
        targetValue = targetAccent,
        animationSpec = tween(220),
        label = "${prayer.name} accent"
    )
    var hasObservedState by remember(prayer.name) { mutableStateOf(false) }
    val dotScale = remember(prayer.name) { Animatable(1f) }
    val rippleScale = remember(prayer.name) { Animatable(0.7f) }
    val rippleAlpha = remember(prayer.name) { Animatable(0f) }
    val activePulse = rememberInfiniteTransition(label = "${prayer.name} next prayer pulse")
    val currentScale by activePulse.animateFloat(
        initialValue = 1f,
        targetValue = if (isCurrent && !prayer.completed) 1.08f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "${prayer.name} active scale"
    )

    LaunchedEffect(prayer.completed) {
        if (!hasObservedState) {
            hasObservedState = true
            return@LaunchedEffect
        }
        rippleScale.snapTo(0.7f)
        rippleAlpha.snapTo(0.75f)
        dotScale.snapTo(0.82f)
        coroutineScope {
            launch {
                dotScale.animateTo(
                    1.2f,
                    spring(dampingRatio = 0.48f, stiffness = 520f)
                )
                dotScale.animateTo(1f, tween(120))
            }
            launch {
                rippleScale.animateTo(2.2f, tween(550, easing = FastOutSlowInEasing))
            }
            launch {
                rippleAlpha.animateTo(0f, tween(550))
            }
        }
    }

    Column(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(if (isCurrent) colors.surface else colors.surfaceContainer)
            .border(1.dp, if (isCurrent) colors.secondary else colors.outlineVariant, RoundedCornerShape(16.dp))
            .hapticClickable(enabled = enabled, onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = prayer.name,
            color = colors.onSurfaceVariant,
            fontSize = 10.sp,
            lineHeight = 14.sp,
            maxLines = 1
        )
        Spacer(modifier = Modifier.size(4.dp))
        Box(
            modifier = Modifier
                .size(30.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .scale(rippleScale.value)
                    .clip(CircleShape)
                    .background(colors.tertiary.copy(alpha = rippleAlpha.value))
            )
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .scale(dotScale.value * currentScale)
                    .clip(CircleShape)
                    .then(
                        if (prayer.completed || isCurrent) Modifier.background(accent)
                        else Modifier.background(colors.outlineVariant)
                    )
                    .then(
                        if (isCurrent && !prayer.completed) {
                            Modifier.border(2.dp, colors.secondary.copy(alpha = 0.3f), CircleShape)
                        } else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = prayer.completed,
                    transitionSpec = {
                        (fadeIn(tween(120)) + scaleIn(tween(180), initialScale = 0.6f))
                            .togetherWith(fadeOut(tween(100)) + scaleOut(tween(100), targetScale = 0.6f))
                    },
                    label = "${prayer.name} completion icon"
                ) { completed ->
                    if (completed) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "${prayer.name} completed",
                            tint = colors.onPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(if (isCurrent) 5.dp else 4.dp)
                                .clip(CircleShape)
                                .background(if (isCurrent) Color.White else accent)
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.size(3.dp))
        Text(
            text = prayer.time,
            color = accent,
            fontSize = 10.sp,
            lineHeight = 14.sp,
            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
            maxLines = 1
        )
    }
}

@Composable
private fun rememberSweepingProgress(target: Float, durationMillis: Int): Float {
    val animated = remember { Animatable(0f) }
    LaunchedEffect(target) {
        animated.animateTo(
            target.coerceIn(0f, 1f),
            animationSpec = tween(durationMillis, easing = FastOutSlowInEasing)
        )
    }
    return animated.value
}
