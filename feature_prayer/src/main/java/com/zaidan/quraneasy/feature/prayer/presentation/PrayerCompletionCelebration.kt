package com.zaidan.quraneasy.feature.prayer.presentation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.delay

private val PrayerCelebrationBackground = Color(0xFFE6F5EC)
private val PrayerCelebrationBorder = Color(0xFF8FC8A8)
private val PrayerCelebrationScrim = Color(0x2218213A)
private val DefaultCardBackground = Color.White
private val DefaultCardBorder = Color(0xFFD9D9DE)

private const val CompletionPulseInDurationMs = 140
private const val CompletionPulseOutDurationMs = 180
private const val CompletionHoldDurationMs = 240
private const val CompletionConfettiDurationMs = 1600L

data class PrayerCompletionCelebrationState(
    val scale: Float,
    val backgroundColor: Color,
    val borderColor: Color,
    val confettiVisible: Boolean,
    val confettiAlpha: Float
)

@Composable
fun rememberPrayerCompletionCelebrationState(
    isAllCompleted: Boolean,
    onCelebrationTriggered: () -> Unit
): PrayerCompletionCelebrationState {
    var hasInitializedCompletionState by rememberSaveable { mutableStateOf(false) }
    var previousAllCompleted by rememberSaveable { mutableStateOf(false) }
    val celebrationScale = remember { Animatable(1f) }
    val celebrationGlow = remember { Animatable(0f) }
    val celebrationConfettiAlpha = remember { Animatable(0f) }
    var showCompletionConfetti by remember { mutableStateOf(false) }

    LaunchedEffect(isAllCompleted) {
        if (!hasInitializedCompletionState) {
            previousAllCompleted = isAllCompleted
            hasInitializedCompletionState = true
            return@LaunchedEffect
        }

        if (!previousAllCompleted && isAllCompleted) {
            onCelebrationTriggered()

            celebrationGlow.snapTo(0f)
            celebrationScale.snapTo(1f)
            celebrationConfettiAlpha.snapTo(0f)

            showCompletionConfetti = true
            celebrationConfettiAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = CompletionPulseInDurationMs,
                    easing = FastOutSlowInEasing
                )
            )
            celebrationGlow.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = CompletionPulseInDurationMs,
                    easing = FastOutSlowInEasing
                )
            )
            celebrationScale.animateTo(
                targetValue = 1.02f,
                animationSpec = tween(
                    durationMillis = CompletionPulseInDurationMs,
                    easing = FastOutSlowInEasing
                )
            )

            delay(CompletionHoldDurationMs.toLong())

            celebrationGlow.animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = CompletionPulseOutDurationMs,
                    easing = FastOutSlowInEasing
                )
            )
            celebrationScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = CompletionPulseOutDurationMs,
                    easing = FastOutSlowInEasing
                )
            )

            delay(CompletionConfettiDurationMs)

            celebrationConfettiAlpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = CompletionPulseOutDurationMs,
                    easing = FastOutSlowInEasing
                )
            )
            showCompletionConfetti = false
        }

        previousAllCompleted = isAllCompleted
    }

    return PrayerCompletionCelebrationState(
        scale = celebrationScale.value,
        backgroundColor = lerp(DefaultCardBackground, PrayerCelebrationBackground, celebrationGlow.value),
        borderColor = lerp(DefaultCardBorder, PrayerCelebrationBorder, celebrationGlow.value),
        confettiVisible = showCompletionConfetti || celebrationConfettiAlpha.value > 0f,
        confettiAlpha = celebrationConfettiAlpha.value
    )
}

@Composable
fun PrayerCompletionConfettiOverlay(
    alpha: Float,
    modifier: Modifier = Modifier
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset("prayer_completion_confetti.json")
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = alpha > 0f,
        iterations = 1
    )

    Box(
        modifier = modifier
            .graphicsLayer { this.alpha = alpha }
            .background(PrayerCelebrationScrim)
            .padding(8.dp)
    ) {
        if (composition != null) {
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
