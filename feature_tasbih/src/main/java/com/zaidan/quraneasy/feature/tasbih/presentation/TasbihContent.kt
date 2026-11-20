package com.zaidan.quraneasy.feature.tasbih.presentation

import androidx.annotation.RequiresPermission
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaidan.quraneasy.core.theme.QuranFont

private data class Dhikr(val title: String, val arabic: String, val meaning: String)
private val adhkar = listOf(
    Dhikr("SubhanAllah", "سُبْحَانَ اللَّهِ", "Glory be to Allah"),
    Dhikr("Alhamdulillah", "الْحَمْدُ لِلَّهِ", "All praise is to Allah"),
    Dhikr("Allahu Akbar", "اللَّهُ أَكْبَرُ", "Allah is the Greatest"),
)

@RequiresPermission("android.permission.VIBRATE")
@Composable
fun TasbihContent(
    uiState: TasbihUiState,
    onBackClick: () -> Unit,
    onResetClick: () -> Unit,
    onSwipeUp: () -> Unit,
    onTargetChange: (Int) -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    var showTarget by rememberSaveable { mutableStateOf(false) }
    var showReset by rememberSaveable { mutableStateOf(false) }
    var selected by rememberSaveable { mutableIntStateOf(0) }
    var haptics by rememberSaveable { mutableStateOf(true) }
    val feedback = LocalHapticFeedback.current
    val dhikr = adhkar[selected]
    val increment = {
        if (haptics) feedback.performHapticFeedback(HapticFeedbackType.LongPress)
        onSwipeUp()
    }
    val latestIncrement by rememberUpdatedState(increment)
    Column(
        Modifier.fillMaxSize().background(colors.background)
            .verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Surface(color = colors.surfaceContainerLow, shape = RoundedCornerShape(16.dp)) {
            Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(color = colors.primary, shape = RoundedCornerShape(12.dp)) {
                    Icon(Icons.Outlined.Spa, null, Modifier.padding(12.dp).size(24.dp), tint = colors.onPrimary)
                }
                Column(Modifier.weight(1f)) {
                    Text(dhikr.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(dhikr.meaning, style = MaterialTheme.typography.bodySmall)
                }
                Text(dhikr.arabic, fontFamily = QuranFont, fontSize = 24.sp, lineHeight = 40.sp)
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            FilledTonalButton(onClick = { showTarget = true }, colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = colors.secondaryContainer, contentColor = colors.onSecondaryContainer)) {
                Text("Target: ${uiState.target}")
            }
            IconButton(onClick = { showReset = true }) {
                Icon(Icons.Outlined.Refresh, "Reset counter", tint = colors.secondary)
            }
            FilterChip(selected = haptics, onClick = { haptics = !haptics }, label = {
                Text(if (haptics) "Haptic On" else "Haptic Off")
            })
        }
        Surface(
            onClick = increment, color = colors.surfaceContainerLowest,
            shape = RoundedCornerShape(24.dp), shadowElevation = 1.dp,
            modifier = Modifier.fillMaxWidth().pointerInput(Unit) {
                var drag = 0f
                detectVerticalDragGestures(
                    onDragStart = { drag = 0f }, onDragCancel = { drag = 0f },
                    onDragEnd = { if (drag < -64.dp.toPx()) latestIncrement() },
                    onVerticalDrag = { change, amount -> drag += amount; change.consume() },
                )
            },
        ) {
            Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)) {
                Surface(shape = CircleShape, color = colors.surfaceVariant) {
                    Text("DAILY REMEMBRANCE", Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.labelMedium)
                }
                Box(Modifier.size(240.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = { (uiState.count.toFloat() / uiState.target.coerceAtLeast(1)).coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxSize(), color = colors.secondary,
                        trackColor = colors.surfaceVariant, strokeWidth = 8.dp,
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(uiState.count.toString(), fontSize = 64.sp, fontWeight = FontWeight.Bold)
                        Text("of ${uiState.target}", color = colors.onSurfaceVariant)
                    }
                }
                Text(if (uiState.count >= uiState.target) "Target reached · Alhamdulillah" else "Tap here or swipe up to count",
                    style = MaterialTheme.typography.labelLarge, textAlign = TextAlign.Center)
            }
        }
        Text("COMMON ADHKAR", style = MaterialTheme.typography.labelMedium, color = colors.onSurfaceVariant)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            adhkar.forEachIndexed { index, item ->
                Surface(onClick = { selected = index }, modifier = Modifier.weight(1f),
                    color = if (selected == index) colors.secondaryContainer else colors.surfaceContainerLow,
                    shape = RoundedCornerShape(12.dp)) {
                    Column(Modifier.padding(10.dp)) {
                        Text(item.title, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(item.arabic, Modifier.fillMaxWidth().padding(top = 8.dp),
                            fontFamily = QuranFont, fontSize = 22.sp, lineHeight = 36.sp,
                            textAlign = TextAlign.End, color = colors.secondary)
                    }
                }
            }
        }
    }
    if (showTarget) TargetPickerDialog(uiState.target, { showTarget = false }) {
        showTarget = false
        onTargetChange(it)
    }
    if (showReset) AlertDialog(onDismissRequest = { showReset = false },
        title = { Text("Reset counter?") }, text = { Text("Your current count will return to zero.") },
        confirmButton = { TextButton(onClick = { showReset = false; onResetClick() }) { Text("Reset") } },
        dismissButton = { TextButton(onClick = { showReset = false }) { Text("Cancel") } })
}
