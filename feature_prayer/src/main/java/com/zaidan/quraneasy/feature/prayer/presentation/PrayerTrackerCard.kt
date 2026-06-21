package com.zaidan.quraneasy.feature.prayer.presentation

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.zaidan.quraneasy.core.R
import com.zaidan.quraneasy.core.feedbacks.rememberPrayerTickFeedbackController
import com.zaidan.quraneasy.core.theme.AppPrimaryGradientBottom
import com.zaidan.quraneasy.core.theme.AppPrimaryText
import com.zaidan.quraneasy.core.theme.AppSecondaryText
import com.zaidan.quraneasy.core.theme.AppSoftSurface
import com.zaidan.quraneasy.core.theme.AppSurface
import com.zaidan.quraneasy.feature.prayer.presentation.viewmodel.PrayerViewModel

private val prayerCardHorizontalPadding = 12.dp
private val prayerCardVerticalPadding = 8.dp
private val titleFontSize = 20.sp
private val subtitleFontSize = 14.sp
private const val TAG = "PrayerTrackerCard"
private val PrayerAccentDeep = AppPrimaryGradientBottom
private val PrayerAccentSoft = Color(0xFFE7ECF8)
private val PrayerSuccessTint = Color(0xFF1B8F6A)
private val PrayerRowBorder = Color(0xFFDDE3EE)
private val PrayerIndicatorTrack = Color(0xFFE8EDF6)
private val PrayerHeaderSurface = Color(0xFFF3F6FC)
private val PrayerLocationText = Color(0xFF4A5875)

@Preview(showBackground = true)
@Composable
fun PrayerTrackerCardPreview() {
    PrayerTrackerCard(
        uiState = PrayerCardUiState(
            cardMode = PrayerCardMode.Ready,
            prayers = listOf(
                PrayerRowUi("Fajr", "04:07", true, true),
                PrayerRowUi("Dhuhr", "12:22", true, true),
                PrayerRowUi("Asr", "15:54", false, true),
                PrayerRowUi("Maghrib", "19:21", false, true),
                PrayerRowUi("Isha", "20:38", false, true)
            )
        ),
        onTogglePrayer = { _, _ -> },
        onCardClick = null,
        onAllPrayersCompleted = {}
    )
}

@Composable
fun PrayerTrackerCardFeature(prayerViewModel: PrayerViewModel) {
    val uiState by prayerViewModel.uiStateFlow.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    val activity = LocalContext.current.findActivity()
    val feedbackController = rememberPrayerTickFeedbackController()
    var hasRequestedLocationPermission by rememberSaveable { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        Log.i(TAG, "permissionLauncherResult(): result=$result")
        prayerViewModel.startObservingLocation()
    }

    DisposableEffect(lifecycleOwner, prayerViewModel) {
        Log.i(TAG, "PrayerTrackerCardFeature(): attaching lifecycle observer")
        val observer = LifecycleEventObserver { _, event ->
            Log.d(TAG, "PrayerTrackerCardFeature(): lifecycle event=$event")
            when (event) {
                Lifecycle.Event.ON_RESUME -> prayerViewModel.startObservingLocation()
                Lifecycle.Event.ON_PAUSE -> prayerViewModel.stopObservingLocation()
                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            Log.i(TAG, "PrayerTrackerCardFeature(): disposing lifecycle observer")
            lifecycleOwner.lifecycle.removeObserver(observer)
            prayerViewModel.stopObservingLocation()
        }
    }

    PrayerTrackerCard(
        uiState = uiState,
        onTogglePrayer = { index, isCompleted ->
            if (isCompleted) {
                feedbackController.playUncheckFeedback()
            } else {
                feedbackController.playCheckFeedback()
            }
            prayerViewModel.togglePrayer(index)
        },
        onCardClick = {
            Log.i(TAG, "onCardClick(): cardMode=${uiState.cardMode}")
            if (uiState.cardMode == PrayerCardMode.NoPermission) {
                val permissions = arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                val permanentlyDenied = activity?.let { hostActivity ->
                    permissions.all { permission ->
                        val granted = ContextCompat.checkSelfPermission(
                            hostActivity,
                            permission
                        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                        val shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                            hostActivity,
                            permission
                        )
                        Log.d(
                            TAG,
                            "permissionCheck(): permission=$permission granted=$granted shouldShowRationale=$shouldShowRationale hasRequestedBefore=$hasRequestedLocationPermission"
                        )
                        hasRequestedLocationPermission && !granted && !shouldShowRationale
                    }
                } == true

                if (permanentlyDenied) {
                    Log.i(TAG, "onCardClick(): permission permanently denied, opening app settings")
                    activity.openAppSettings()
                } else {
                    Log.i(TAG, "onCardClick(): launching permission popup")
                    hasRequestedLocationPermission = true
                    permissionLauncher.launch(permissions)
                }
            }
        },
        onAllPrayersCompleted = feedbackController::playAllPrayersCompletedFeedback
    )
}

@Composable
fun PrayerTrackerCard(
    uiState: PrayerCardUiState,
    onCardClick: (() -> Unit)? = null,
    onTogglePrayer: (Int, Boolean) -> Unit,
    onAllPrayersCompleted: () -> Unit = {}
) {
    val canTogglePrayers = uiState.cardMode == PrayerCardMode.Ready ||
        uiState.cardMode == PrayerCardMode.ReadyOfflineCache
    val canRequestPermission = uiState.cardMode == PrayerCardMode.NoPermission &&
        onCardClick != null
    val prayers = when (uiState.cardMode) {
        PrayerCardMode.Ready,
        PrayerCardMode.ReadyOfflineCache -> uiState.prayers
        PrayerCardMode.NoPermission,
        PrayerCardMode.Loading,
        PrayerCardMode.Unavailable -> PrayerRowUi.placeholderList()
    }
    val statusText = when (uiState.cardMode) {
        PrayerCardMode.Ready,
        PrayerCardMode.ReadyOfflineCache -> {
            val completedCount = prayers.count { it.completed }
            "$completedCount of ${prayers.size} completed"
        }
        PrayerCardMode.Loading -> "Loading prayer times..."
        PrayerCardMode.NoPermission -> "Location permission required"
        PrayerCardMode.Unavailable -> "Prayer times unavailable"
    }
    val banner = uiState.banner ?: when (uiState.cardMode) {
        PrayerCardMode.NoPermission -> "Tap to enable location for prayer times"
        PrayerCardMode.ReadyOfflineCache -> "Offline. Showing saved timings."
        else -> null
    }
    val locationLabel = uiState.locationLabel
    val totalPrayerCount = prayers.count { it.completed }
    val progress = if (prayers.isNotEmpty()) totalPrayerCount / prayers.size.toFloat() else 0f
    val cardAlpha = if (canTogglePrayers) 1f else 0.55f
    val currentPrayerProgress = remember(prayers) { calculateCurrentPrayerProgress(prayers) }
    val isAllCompleted = prayers.size == 5 && prayers.all { it.completed }
    val celebrationState = rememberPrayerCompletionCelebrationState(
        isAllCompleted = isAllCompleted,
        onCelebrationTriggered = onAllPrayersCompleted
    )

    Log.i(
        TAG,
        "PrayerTrackerCard(): cardMode=${uiState.cardMode}, canToggle=$canTogglePrayers, canRequestPermission=$canRequestPermission, locationLabel=$locationLabel, prayerCount=${prayers.size}"
    )


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer(
                scaleX = celebrationState.scale,
                scaleY = celebrationState.scale
            )
            .clickable(enabled = canRequestPermission) { onCardClick?.invoke() },
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = celebrationState.backgroundColor),
        border = BorderStroke(1.dp, celebrationState.borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            if (banner != null) {
                Text(
                    text = banner,
                    color = Color.Black,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .alpha(0.8f)
                        .padding(16.dp),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Column(
                modifier = Modifier
                    .alpha(cardAlpha)
                    .padding(
                        horizontal = prayerCardHorizontalPadding,
                        vertical = prayerCardVerticalPadding
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    PrayerHeaderSurface,
                                    AppSurface
                                )
                            )
                        )
                        .padding(horizontal = 16.dp, vertical = 18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        if (locationLabel != null) {
                            Text(
                                text = locationLabel,
                                color = PrayerLocationText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        Text(
                            text = "Prayer Tracker",
                            color = AppPrimaryText,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = statusText,
                            color = if (canTogglePrayers) PrayerSuccessTint else AppSecondaryText,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (currentPrayerProgress != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            LinearProgressIndicator(
                                progress = { progress.coerceIn(0f, 1f) },
                                modifier = Modifier.fillMaxWidth()
                                    .height(10.dp),
                                color = PrayerAccentDeep,
                                trackColor = PrayerAccentSoft
                            )
                        }
                    }
                    Spacer(modifier = Modifier.size(16.dp))
                    PrayerSummaryIndicator(
                        progress = currentPrayerProgress?.progress ?: progress,
                        durationTimeText = currentPrayerProgress?.timeLeftLabel ?: "--",
                        timeLeftLabel = "Left for",
                        prayerNameLabel = currentPrayerProgress?.name
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
                prayers.forEachIndexed { index, prayer ->
                    PrayerRow(
                        name = prayer.name,
                        time = prayer.time,
                        completed = prayer.completed,
                        enabled = prayer.enabled && canTogglePrayers,
                        onClick = {
                            if (prayer.enabled && canTogglePrayers) {
                                onTogglePrayer(index, prayer.completed)
                            }
                        }
                    )
                    if (index != prayers.lastIndex) {
                        Spacer(modifier = Modifier.height(14.dp))
                    }
                }
            }
            if (celebrationState.confettiVisible) {
                PrayerCompletionConfettiOverlay(
                    alpha = celebrationState.confettiAlpha,
                    modifier = Modifier.matchParentSize()
                )
            }
        }
    }
}

@Composable
private fun PrayerRow(
    name: String,
    time: String,
    completed: Boolean,
    enabled: Boolean,
    onClick: (() -> Unit)? = null
) {
    var tickTrigger by remember(name, time) { mutableIntStateOf(0) }
    var rowShakeTrigger by remember(name, time) { mutableIntStateOf(0) }
    val tickScale = remember { Animatable(1f) }
    val rowOffsetX = remember { Animatable(0f) }

    LaunchedEffect(tickTrigger) {
        if (tickTrigger == 0) return@LaunchedEffect
        tickScale.snapTo(0.84f)
        tickScale.animateTo(
            targetValue = 1.12f,
            animationSpec = tween(durationMillis = 110, easing = FastOutSlowInEasing)
        )
        tickScale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 140, easing = FastOutSlowInEasing)
        )
    }

    LaunchedEffect(rowShakeTrigger) {
        if (rowShakeTrigger == 0) return@LaunchedEffect
        rowOffsetX.snapTo(0f)
        rowOffsetX.animateTo(-18f, tween(durationMillis = 45))
        rowOffsetX.animateTo(14f, tween(durationMillis = 70))
        rowOffsetX.animateTo(-10f, tween(durationMillis = 65))
        rowOffsetX.animateTo(6f, tween(durationMillis = 55))
        rowOffsetX.animateTo(0f, tween(durationMillis = 50))
    }

    Log.d(TAG, "PrayerRow(): name=$name time=$time completed=$completed enabled=$enabled")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { translationX = rowOffsetX.value }
            .clip(RoundedCornerShape(16.dp))
            .background(AppSoftSurface)
            .border(1.dp, PrayerRowBorder, RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (completed) {
                Image(
                    painter = painterResource(id = R.drawable.check_tick),
                    contentDescription = "Tick",
                    modifier = Modifier
                        .size(40.dp)
                        .scale(tickScale.value)
                        .clip(CircleShape)
                        .border(2.dp, PrayerAccentSoft, CircleShape)
                        .clickable(
                            enabled = enabled,
                            onClick = {
                                tickTrigger++
                                rowShakeTrigger++
                                onClick?.invoke()
                            }
                        )
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .scale(tickScale.value)
                        .clip(CircleShape)
                        .border(2.dp, PrayerAccentSoft, CircleShape)
                        .clickable(
                            enabled = enabled,
                            onClick = {
                                tickTrigger++
                                onClick?.invoke()
                            }
                        )
                )
            }

            Spacer(modifier = Modifier.size(14.dp))
            Text(
                text = name,
                color = AppPrimaryText,
                fontSize = titleFontSize,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = time,
            color = AppSecondaryText,
            fontSize = subtitleFontSize,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun PrayerSummaryIndicator(
    progress: Float,
    durationTimeText: String,
    timeLeftLabel: String,
    prayerNameLabel: String?
) {
    Box(
        modifier = Modifier.size(112.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier.size(112.dp),
            strokeWidth = 12.dp,
            color = PrayerAccentDeep,
            trackColor = PrayerIndicatorTrack
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text(
                text = durationTimeText,
                color = AppPrimaryText,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = timeLeftLabel,
                color = AppSecondaryText,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            Text(
                text = prayerNameLabel ?:"Fajr",
                color = AppSecondaryText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun Context.openAppSettings() {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    )
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
}

private fun Context.findActivity(): Activity? {
    var current: Context? = this
    while (current is ContextWrapper) {
        if (current is Activity) return current
        current = current.baseContext
    }
    return null
}
