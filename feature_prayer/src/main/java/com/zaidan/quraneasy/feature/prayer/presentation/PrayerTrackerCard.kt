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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.zaidan.quraneasy.core.theme.AppPrimaryGradientBottom
import com.zaidan.quraneasy.core.theme.AppPrimaryGradientTop
import com.zaidan.quraneasy.core.theme.AppPrimaryText
import com.zaidan.quraneasy.core.theme.AppSecondaryText
import com.zaidan.quraneasy.core.theme.AppSoftSurface
import com.zaidan.quraneasy.core.theme.AppSurface
import com.zaidan.quraneasy.core.theme.AppSurfaceBorder
import com.zaidan.quraneasy.feature.prayer.presentation.viewmodel.PrayerViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private val prayerCardHorizontalPadding = 12.dp
private val prayerCardVerticalPadding = 8.dp
private val titleFontSize = 20.sp
private val subtitleFontSize = 14.sp
private const val TAG = "PrayerTrackerCard"
private val PrayerAccent = AppPrimaryGradientTop
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
        onTogglePrayer = {},
        onCardClick = null,
    )
}

@Composable
fun PrayerTrackerCardFeature(prayerViewModel: PrayerViewModel) {
    val uiState by prayerViewModel.uiStateFlow.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    val activity = LocalContext.current.findActivity()
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
        onTogglePrayer = prayerViewModel::togglePrayer,
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
                    activity?.openAppSettings()
                } else {
                    Log.i(TAG, "onCardClick(): launching permission popup")
                    hasRequestedLocationPermission = true
                    permissionLauncher.launch(permissions)
                }
            }
        }
    )
}

@Composable
fun PrayerTrackerCard(
    uiState: PrayerCardUiState,
    onCardClick: (() -> Unit)? = null,
    onTogglePrayer: (Int) -> Unit
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
    val isContentReady = canTogglePrayers

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
    val totalPrayerSlots = prayers.size
    val progress = if (prayers.isNotEmpty()) totalPrayerCount / prayers.size.toFloat() else 0f
    val cardAlpha = if (isContentReady) 1f else 0.55f
    val nextPrayerProgress = rememberNextPrayerProgress(prayers)

    Log.i(
        TAG,
        "PrayerTrackerCard(): cardMode=${uiState.cardMode}, canToggle=$canTogglePrayers, canRequestPermission=$canRequestPermission, locationLabel=$locationLabel, prayerCount=${prayers.size}"
    )


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = canRequestPermission) { onCardClick?.invoke() },
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = AppSurface),
        border = BorderStroke(1.dp, AppSurfaceBorder),
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
                        if (nextPrayerProgress != null) {
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
                        progress = nextPrayerProgress?.progress ?: progress,
                        primaryText = nextPrayerProgress?.timeLeftLabel ?: "--",
                        secondaryText = nextPrayerProgress?.name ?: "Next prayer"
                    )
                }
                prayers.forEachIndexed { index, prayer ->
                    PrayerRow(
                        name = prayer.name,
                        time = prayer.time,
                        completed = prayer.completed,
                        enabled = prayer.enabled && canTogglePrayers,
                        onClick = {
                            if (prayer.enabled && canTogglePrayers) {
                                onTogglePrayer(index)
                            }
                        }
                    )
                    if (index != prayers.lastIndex) {
                        Spacer(modifier = Modifier.height(14.dp))
                    }
                }
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
    Log.d(TAG, "PrayerRow(): name=$name time=$time completed=$completed enabled=$enabled")
    Row(
        modifier = Modifier
            .fillMaxWidth()
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
                        .clip(CircleShape)
                        .border(2.dp, PrayerAccentSoft, CircleShape)
                        .clickable(enabled = enabled, onClick = { onClick?.invoke() })
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(2.dp, PrayerAccentSoft, CircleShape)
                        .clickable(enabled = enabled, onClick = { onClick?.invoke() })
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
    primaryText: String,
    secondaryText: String
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = primaryText,
                color = AppPrimaryText,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = secondaryText,
                color = AppSecondaryText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun rememberNextPrayerProgress(prayers: List<PrayerRowUi>): NextPrayerProgress? {
    if (prayers.isEmpty() || prayers.any { it.time == "--:--" }) return null

    val calendar = Calendar.getInstance()
    val nowMinutes = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)
    val prayerMinutes = prayers.mapNotNull { prayer ->
        parsePrayerMinutes(prayer.time)?.let { prayer.name to it }
    }
    if (prayerMinutes.isEmpty()) return null

    val nextPrayer = prayerMinutes.firstOrNull { (_, minutes) -> minutes > nowMinutes }
        ?: prayerMinutes.firstOrNull()
        ?: return null
    val nextMinutes = nextPrayer.second
    val previousMinutes = prayerMinutes.lastOrNull { (_, minutes) -> minutes <= nowMinutes }?.second
        ?: (prayerMinutes.lastOrNull()?.second?.minus(24 * 60))
        ?: return null

    val adjustedNextMinutes = if (nextMinutes <= nowMinutes) nextMinutes + 24 * 60 else nextMinutes
    val adjustedNowMinutes = if (nextMinutes <= nowMinutes) nowMinutes + 24 * 60 else nowMinutes
    val segmentDuration = (adjustedNextMinutes - previousMinutes).coerceAtLeast(1)
    val elapsed = (adjustedNowMinutes - previousMinutes).coerceIn(0, segmentDuration)
    val remaining = (adjustedNextMinutes - adjustedNowMinutes).coerceAtLeast(0)

    return NextPrayerProgress(
        name = nextPrayer.first,
        progress = elapsed.toFloat() / segmentDuration.toFloat(),
        timeLeftLabel = formatDurationLabel(remaining),
        time = formatPrayerDisplayTime(nextMinutes)
    )
}

private fun parsePrayerMinutes(prayerTime: String): Int? {
    val parsed = runCatching {
        SimpleDateFormat("HH:mm", Locale.US).parse(prayerTime)
    }.getOrNull() ?: return null
    val calendar = Calendar.getInstance().apply { this.time = parsed }
    return calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)
}

private fun formatDurationLabel(totalMinutes: Int): String {
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    return if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
}

private fun formatPrayerDisplayTime(totalMinutes: Int): String {
    val normalizedMinutes = ((totalMinutes % (24 * 60)) + (24 * 60)) % (24 * 60)
    val hours = normalizedMinutes / 60
    val minutes = normalizedMinutes % 60
    return String.format(Locale.US, "%02d:%02d", hours, minutes)
}

private data class NextPrayerProgress(
    val name: String,
    val progress: Float,
    val timeLeftLabel: String,
    val time: String
)

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
