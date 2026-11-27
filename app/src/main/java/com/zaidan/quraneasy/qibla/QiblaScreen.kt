package com.zaidan.quraneasy.qibla

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.math.*

@Composable
fun QiblaScreen() {
    val context = LocalContext.current
    val preferences = remember { context.getSharedPreferences("qibla", Context.MODE_PRIVATE) }
    var location by remember { mutableStateOf(if (preferences.contains("latitude")) QiblaLocation(preferences.getString("latitude", "0")!!.toDouble(), preferences.getString("longitude", "0")!!.toDouble(), preferences.getString("label", "Saved location")!!) else null) }
    var automatic by rememberSaveable { mutableStateOf(preferences.getBoolean("automatic", true)) }
    var permission by remember { mutableStateOf(context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED || context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) }
    var status by remember { mutableStateOf("") }
    var mapStatus by remember { mutableStateOf("") }
    var showCity by rememberSaveable { mutableStateOf(false) }
    var recenter by remember { mutableIntStateOf(0) }
    fun save(p: QiblaLocation, auto: Boolean) {
        location = p; automatic = auto
        preferences.edit().putString("latitude", p.latitude.toString()).putString("longitude", p.longitude.toString()).putString("label", p.label).putBoolean("automatic", auto).apply()
    }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { grants ->
        permission = grants.values.any { it }
        if (!permission) { status = "Location permission denied. Choose a city to continue."; showCity = true }
    }
    ObserveLocation(context, automatic && permission, { save(it, true) }, { status = it })
    val heading = rememberHeading(context, location)
    val colors = MaterialTheme.colorScheme
    val dark = colors.background.luminance() < .2f
    val glass = if (dark) Color(0xEE0E151C) else colors.surface.copy(alpha = .94f)
    val haptic = LocalHapticFeedback.current
    val bearing = location?.let(QiblaMath::bearing)
    val distance = location?.let(QiblaMath::distanceKm)
    val turn = if (bearing != null && heading.degrees != null) QiblaMath.turn(heading.degrees, bearing) else null
    val atKaaba = distance != null && distance < .05
    val aligned = !atKaaba && turn != null && abs(turn) <= 5 && heading.reliable
    val canGuide = location != null && !atKaaba && heading.available && heading.reliable && turn != null
    val guidanceColor = when {
        !canGuide -> colors.onSurfaceVariant
        aligned -> if (dark) Color(0xFF4ADE80) else Color(0xFF15803D)
        else -> if (dark) Color(0xFFFF7878) else Color(0xFFB91C1C)
    }
    val guidanceText = when {
        location == null -> "Set your location"
        atKaaba -> "You are near the Kaaba"
        !heading.available -> "Compass unavailable"
        heading.degrees == null -> "Waiting for compass…"
        !heading.reliable -> "Calibrate your compass"
        aligned -> "Facing Qibla"
        turn!! < 0 -> "Turn left"
        else -> "Turn right"
    }
    var latched by remember { mutableStateOf(false) }
    val shouldRearm = turn == null || abs(turn) > 8 || !heading.reliable
    LaunchedEffect(aligned, shouldRearm) {
        if (shouldRearm) latched = false
        if (aligned && !latched) { delay(400); haptic.performHapticFeedback(HapticFeedbackType.LongPress); latched = true }
    }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 4.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(Modifier.fillMaxWidth().height(375.dp).clip(RoundedCornerShape(24.dp)).background(colors.surfaceVariant)) {
            QiblaMap(location, dark, colors.primary, recenter,
                heading = heading.degrees.takeIf { canGuide }, arrowColor = guidanceColor,
                modifier = Modifier.matchParentSize()) { mapStatus = it }
            Surface(Modifier.align(Alignment.TopStart).padding(12.dp), shape = RoundedCornerShape(20.dp), color = glass, border = BorderStroke(1.dp, colors.onSurface.copy(alpha = .08f))) {
                Text(if (bearing == null) "Choose your location" else "Direction to Makkah", Modifier.padding(horizontal = 12.dp, vertical = 8.dp), style = MaterialTheme.typography.labelMedium)
            }
            Surface(Modifier.align(Alignment.TopEnd).padding(top = 12.dp, end = 12.dp), shape = CircleShape, color = Color.Transparent) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Compass(heading.degrees.takeIf { heading.reliable })
                }
            }
            if (aligned) Surface(
                modifier = Modifier.align(Alignment.TopStart).padding(start = 12.dp, top = 60.dp, end = 100.dp),
                shape = RoundedCornerShape(20.dp), color = glass,
                border = BorderStroke(1.dp, guidanceColor.copy(alpha = .3f))
            ) {
                Text("✓ Facing Qibla • Aligned", Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    style = MaterialTheme.typography.labelMedium, color = guidanceColor)
            }
            Surface(Modifier.align(Alignment.BottomStart).padding(start = 12.dp, bottom = 36.dp, end = 68.dp),
                shape = CircleShape, color = glass,
                border = BorderStroke(1.dp, colors.onSurface.copy(alpha = .08f))) {
                Row(Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    Box(Modifier.size(7.dp).background(colors.primary, CircleShape))
                    Text("You", fontSize = 11.sp, color = colors.onSurfaceVariant)
                    Text("┄┄┄", fontSize = 11.sp, color = colors.onSurfaceVariant.copy(alpha = .6f))
                    Box(Modifier.size(7.dp).background(Color(0xFFDCA148), CircleShape))
                    Text("Kaaba · Makkah", fontSize = 11.sp, color = colors.onSurfaceVariant)
                }
            }
            FilledIconButton(onClick = { recenter++ }, modifier = Modifier.align(Alignment.BottomEnd).padding(end = 12.dp, bottom = 30.dp)) { Icon(Icons.Default.MyLocation, "Show my location and Kaaba") }
        }
        if (mapStatus.isNotEmpty()) Text(mapStatus, style = MaterialTheme.typography.labelSmall, color = colors.onSurfaceVariant)
        val cardTint by animateColorAsState(
            if (aligned) { if (dark) Color(0xFF34D399) else Color(0xFF15803D) } else colors.primary,
            tween(450), label = "Direction card tint")
        val gradientStart by animateColorAsState(
            if (dark) { if (aligned) Color(0xFF0C2419) else Color(0xFF123039) }
            else { if (aligned) Color(0xFFE0F4E9) else Color(0xFFE5F2F7) }, tween(450), label = "Card gradient start")
        val gradientMiddle by animateColorAsState(
            if (dark) { if (aligned) Color(0xFF0D1D16) else Color(0xFF101F27) }
            else { if (aligned) Color(0xFFF0F8F3) else Color(0xFFF1F6F9) }, tween(450), label = "Card gradient middle")
        val gradientEnd by animateColorAsState(
            if (dark) { if (aligned) Color(0xFF09140F) else Color(0xFF0A141B) }
            else colors.surface, tween(450), label = "Card gradient end")
        Card(
            modifier = Modifier.shadow(12.dp, RoundedCornerShape(16.dp),
                ambientColor = cardTint.copy(alpha = .18f), spotColor = Color.Black.copy(alpha = .28f)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            border = BorderStroke(.8.dp, Brush.linearGradient(listOf(
                cardTint.copy(alpha = .5f), cardTint.copy(alpha = .18f), cardTint.copy(alpha = .32f))))
        ) {
            Row(
                Modifier.fillMaxWidth().drawWithCache {
                    val base = Brush.linearGradient(
                        colorStops = arrayOf(0f to gradientStart, .5f to gradientMiddle, 1f to gradientEnd),
                        start = Offset.Zero, end = Offset(size.width, size.height))
                    val glow = Brush.radialGradient(
                        listOf(cardTint.copy(alpha = .17f), cardTint.copy(alpha = .055f), Color.Transparent),
                        center = Offset(38.dp.toPx(), size.height * .4f), radius = 115.dp.toPx())
                    onDrawBehind {
                        drawRect(base)
                        drawRect(glow)
                        // Fine inner top light gives the surface depth without a heavy outline.
                        drawLine(Color.White.copy(alpha = if (dark) .07f else .5f),
                            Offset(16.dp.toPx(), 1.dp.toPx()), Offset(size.width - 16.dp.toPx(), 1.dp.toPx()), .5.dp.toPx())
                    }
                }.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                QiblaDirectionArrow(
                    turn = if (aligned) 0.0 else turn ?: 0.0,
                    active = canGuide, aligned = aligned, color = guidanceColor, description = guidanceText
                )
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Crossfade(targetState = guidanceText, animationSpec = tween(220), label = "Qibla guidance") { text ->
                        Text(text, fontWeight = FontWeight.Bold, fontSize = 20.sp,
                            color = if (aligned) guidanceColor else colors.onSurface)
                    }
                    Text(when {
                        aligned -> "You are aligned with the Kaaba in Makkah"
                        canGuide -> "Follow the arrow until it turns green"
                        atKaaba -> "You are close to the Kaaba"
                        else -> "Hold your phone flat in front of you"
                    }, style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant)
                    distance?.let {
                        Surface(
                            modifier = Modifier.shadow(6.dp, CircleShape, ambientColor = cardTint.copy(alpha = .25f), spotColor = cardTint.copy(alpha = .18f)),
                            shape = CircleShape, color = gradientEnd.copy(alpha = .88f),
                            border = BorderStroke(.75.dp, cardTint.copy(alpha = .38f))) {
                          Row(Modifier.padding(horizontal = 10.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.LocationOn, null, tint = cardTint, modifier = Modifier.size(13.dp))
                            Text("${String.format(Locale.getDefault(), "%,.0f", it)} km to Makkah",
                                Modifier.semantics {
                                    contentDescription = "Straight-line distance to Makkah: ${it.roundToInt()} kilometres"
                                }, style = MaterialTheme.typography.labelMedium,
                                color = if (aligned) guidanceColor else colors.onSurface)
                          }
                        }
                    }
                }
            }
        }
        if (heading.available && !heading.reliable) Text("Move your phone in a figure eight, hold it flat, and keep away from magnets and metal.", style = MaterialTheme.typography.bodySmall)
        if (!heading.available) Text("This device has no supported compass sensor. Use the map with a separate compass to find the direction.", style = MaterialTheme.typography.bodySmall)
        Text(location?.label ?: "Location is needed to calculate Qibla.", style = MaterialTheme.typography.bodySmall)
        if (status.isNotEmpty() && automatic) Text(status, style = MaterialTheme.typography.bodySmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = {
                automatic = true
                preferences.edit().putBoolean("automatic", true).apply()
                launcher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
                recenter++
            }) { Text("Use location") }
            TextButton(onClick = { showCity = true }) { Text("Choose city") }
        }
        Text("Offline world map · Natural Earth", style = MaterialTheme.typography.labelSmall, color = colors.onSurfaceVariant)
    }
    if (showCity) CityDialog(onDismiss = { showCity = false }, onChoose = { save(it, false); status = ""; showCity = false })
}

@Composable
private fun QiblaDirectionArrow(turn: Double, active: Boolean, aligned: Boolean, color: Color, description: String) {
    val indicatorColor by animateColorAsState(color, tween(320), label = "Qibla alignment color")
    val rotation = remember { Animatable(turn.toFloat()) }
    val alignmentRipple = remember { Animatable(1f) }
    val dark = MaterialTheme.colorScheme.background.luminance() < .2f
    val discBase = if (dark) Color(0xFF0C1C1D) else Color.White
    val discTop by animateColorAsState(
        if (dark && aligned) Color(0xFF144830) else lerp(discBase, color, if (dark) .22f else .12f),
        tween(450), label = "Arrow disc top")
    val discBottom by animateColorAsState(
        if (dark && aligned) Color(0xFF0C2619) else lerp(discBase, color, .06f),
        tween(450), label = "Arrow disc bottom")
    LaunchedEffect(turn, active) {
        if (active) {
            // Follow the shortest arc across -180/180 instead of spinning a full circle.
            val delta = QiblaMath.turn(QiblaMath.normalize(rotation.value.toDouble()), turn).toFloat()
            rotation.animateTo(rotation.value + delta, spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMediumLow))
        } else rotation.snapTo(turn.toFloat())
    }
    LaunchedEffect(aligned, active) {
        if (aligned && active) {
            alignmentRipple.snapTo(0f)
            alignmentRipple.animateTo(1f, tween(700))
        } else alignmentRipple.snapTo(1f)
    }
    Box(
        Modifier.size(60.dp).clearAndSetSemantics { contentDescription = description },
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val radius = size.minDimension / 2 - 2.dp.toPx()
            // A feathered radial halo, then the shaded disc and its two fine rims.
            drawCircle(Brush.radialGradient(listOf(indicatorColor.copy(alpha = .22f), Color.Transparent),
                center = center, radius = radius * 1.5f), radius * 1.5f)
            drawCircle(Brush.verticalGradient(listOf(discTop, discBottom)), radius)
            drawCircle(Brush.linearGradient(listOf(indicatorColor.copy(alpha = .65f), indicatorColor.copy(alpha = .18f))),
                radius, style = Stroke(.8.dp.toPx()))
            drawCircle(Color.White.copy(alpha = if (dark) .08f else .45f), radius - 3.dp.toPx(), style = Stroke(.6.dp.toPx()))
            // A single soft ring celebrates alignment; no continuous distracting pulse.
            if (aligned && active) {
                drawCircle(
                    indicatorColor.copy(alpha = .35f * (1f - alignmentRipple.value)),
                    radius * (.78f + .32f * alignmentRipple.value),
                    style = Stroke(2.dp.toPx())
                )
            }
        }
        if (active) {
            Canvas(Modifier.size(28.dp).rotate(rotation.value)) {
                drawPath(Path().apply {
                    moveTo(size.width * .5f, size.height * .08f)
                    lineTo(size.width * .18f, size.height * .42f)
                    lineTo(size.width * .35f, size.height * .42f)
                    lineTo(size.width * .35f, size.height * .92f)
                    lineTo(size.width * .65f, size.height * .92f)
                    lineTo(size.width * .65f, size.height * .42f)
                    lineTo(size.width * .82f, size.height * .42f)
                    close()
                }, Brush.verticalGradient(listOf(lerp(indicatorColor, Color.White, .18f), indicatorColor)))
            }
        } else {
            Icon(Icons.Default.MyLocation, contentDescription = null, tint = indicatorColor, modifier = Modifier.size(30.dp))
        }
    }
}

@Composable
private fun Compass(heading: Double?) {
    val blink = rememberInfiniteTransition(label = "West indicator")
    val westAlpha by blink.animateFloat(1f, .25f,
        infiniteRepeatable(tween(650), RepeatMode.Reverse), label = "West blink")
    val colors = MaterialTheme.colorScheme
    val dark = colors.background.luminance() < .2f
    val gold = if (dark) Color(0xFFDCA148) else Color(0xFF916315)
    val blue = if (dark) Color(0xFF7DD3FC) else Color(0xFF0284C7)
    val rotation = remember { Animatable(0f) }
    LaunchedEffect(heading) {
        if (heading != null) {
            val delta = QiblaMath.turn(QiblaMath.normalize(rotation.value.toDouble()), -heading)
            rotation.animateTo(rotation.value + delta.toFloat(), tween(180))
        }
    }
    Box(Modifier.size(76.dp).clearAndSetSemantics {
        contentDescription = if (heading == null) "Compass waiting for a reliable heading"
            else "North compass. The blue needle points north. Use the direction card to face Qibla."
    }, contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val r = size.minDimension / 2
            drawCircle(if (dark) Color(0xF20B1219) else colors.surface, r)
            drawCircle(gold.copy(alpha = .4f), r - 1.dp.toPx(), style = Stroke(.8.dp.toPx()))
            drawCircle(Brush.linearGradient(listOf(Color(0xFFF5D796), gold, Color(0xFF8B5E23), gold)), r * .83f, style = Stroke(1.dp.toPx()))
            drawCircle(gold.copy(alpha = .3f), r * .75f, style = Stroke(.5.dp.toPx()))
            if (heading != null) rotate(rotation.value) {
                repeat(60) { tick -> rotate(tick * 6f) {
                    drawLine(gold.copy(alpha = .35f), Offset(center.x, center.y - r * .71f),
                        Offset(center.x, center.y - r * (if (tick % 5 == 0) .66f else .69f)), .5.dp.toPx())
                } }
                repeat(8) { point -> rotate(point * 45f) {
                    val length = r * if (point % 2 == 0) .61f else .44f
                    val width = r * if (point % 2 == 0) .065f else .04f
                    drawPath(Path().apply {
                        moveTo(center.x, center.y - length); lineTo(center.x - width, center.y)
                        lineTo(center.x, center.y); close()
                    }, if (point == 0) blue else gold)
                    drawPath(Path().apply {
                        moveTo(center.x, center.y - length); lineTo(center.x + width, center.y)
                        lineTo(center.x, center.y); close()
                    }, if (point == 0) Color(0xFF0369A1) else gold.copy(alpha = .45f))
                } }
                drawCircle(gold, r * .075f)
                drawCircle(Color(0xFF0A0F12), r * .032f)
            }
        }
        if (heading != null) Box(Modifier.size(59.dp).rotate(rotation.value)) {
            listOf("N" to Alignment.TopCenter, "E" to Alignment.CenterEnd,
                "S" to Alignment.BottomCenter, "W" to Alignment.CenterStart).forEach { (label, alignment) ->
                Text(label, Modifier.align(alignment).rotate(-rotation.value), fontSize = 7.sp,
                    lineHeight = 8.sp, fontWeight = FontWeight.Bold, color = when (label) {
                        "N" -> blue
                        "W" -> Color(0xFFEF4444).copy(alpha = westAlpha)
                        else -> gold
                    })
            }
        } else Icon(Icons.Default.MyLocation, null, tint = colors.onSurfaceVariant, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun CityDialog(onDismiss: () -> Unit, onChoose: (QiblaLocation) -> Unit) {
    val context = LocalContext.current
    var query by rememberSaveable { mutableStateOf("") }
    var results by remember { mutableStateOf(emptyList<QiblaLocation>()) }
    var searching by remember { mutableStateOf(false) }
    val offlineCities = remember { listOf(
        QiblaLocation(28.6139,77.2090,"Delhi"), QiblaLocation(19.0760,72.8777,"Mumbai"),
        QiblaLocation(12.9716,77.5946,"Bengaluru"), QiblaLocation(24.8607,67.0011,"Karachi"),
        QiblaLocation(23.8103,90.4125,"Dhaka"), QiblaLocation(25.2048,55.2708,"Dubai"),
        QiblaLocation(51.5074,-.1278,"London"), QiblaLocation(40.7128,-74.0060,"New York"),
        QiblaLocation(-43.5321,172.6362,"Christchurch"), QiblaLocation(-36.8485,174.7633,"Auckland"),
        QiblaLocation(-33.8688,151.2093,"Sydney"), QiblaMath.kaaba
    ) }
    LaunchedEffect(query) {
        results = offlineCities.filter { it.label.contains(query, true) }
        searching = false
        if (query.length >= 3) {
            delay(450); searching = true
            val found = withContext(Dispatchers.IO) {
                @Suppress("DEPRECATION")
                runCatching { Geocoder(context).getFromLocationName(query, 5).orEmpty().map { QiblaLocation(it.latitude, it.longitude, listOfNotNull(it.locality ?: it.featureName, it.countryName).joinToString(", ")) } }.getOrDefault(emptyList())
            }
            results = (results + found).distinctBy { it.latitude to it.longitude }; searching = false
        }
    }
    AlertDialog(onDismissRequest = onDismiss, title = { Text("Choose location") }, text = {
        Column {
            OutlinedTextField(query, { query = it }, label = { Text("Search city") }, singleLine = true)
            if (searching) Text("Searching…", style = MaterialTheme.typography.bodySmall)
            Column(Modifier.heightIn(max = 250.dp).verticalScroll(rememberScrollState())) {
                results.forEach { p -> TextButton(onClick = { onChoose(p) }) { Text(p.label) } }
            }
            Text("City search uses internet. Listed cities work offline.", style = MaterialTheme.typography.bodySmall)

        }
    }, confirmButton = { TextButton(onClick = onDismiss) { Text("Close") } })
}
