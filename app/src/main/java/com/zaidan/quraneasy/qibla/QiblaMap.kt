package com.zaidan.quraneasy.qibla

import androidx.compose.runtime.*
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.*
import org.maplibre.android.style.layers.PropertyFactory.*
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.*
import java.net.URI

@Composable
internal fun QiblaMap(location: QiblaLocation?, dark: Boolean, accent: Color, recenter: Int, heading: Double?, arrowColor: Color, modifier: Modifier = Modifier, onError: (String) -> Unit) {
    val context = LocalContext.current
    val owner = LocalLifecycleOwner.current
    val error by rememberUpdatedState(onError)
    val view = remember(context) { MapLibre.getInstance(context); MapView(context).apply { onCreate(null) } }
    var map by remember { mutableStateOf<MapLibreMap?>(null) }
    var styleReady by remember { mutableStateOf(false) }
    DisposableEffect(view, owner) {
        var started = false; var resumed = false
        fun start() { if (!started) { view.onStart(); started = true } }
        fun resume() { start(); if (!resumed) { view.onResume(); resumed = true } }
        fun pause() { if (resumed) { view.onPause(); resumed = false } }
        fun stop() { pause(); if (started) { view.onStop(); started = false } }
        val observer = LifecycleEventObserver { _, event -> when (event) {
            Lifecycle.Event.ON_START -> start(); Lifecycle.Event.ON_RESUME -> resume()
            Lifecycle.Event.ON_PAUSE -> pause(); Lifecycle.Event.ON_STOP -> stop(); else -> Unit
        } }
        val failure = MapView.OnDidFailLoadingMapListener { error("Map unavailable. Direction and distance still work offline.") }
        view.addOnDidFailLoadingMapListener(failure)
        owner.lifecycle.addObserver(observer)
        if (owner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) start()
        if (owner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) resume()
        view.getMapAsync {
            it.uiSettings.isRotateGesturesEnabled = false
            it.uiSettings.isTiltGesturesEnabled = false
            it.uiSettings.isCompassEnabled = false
            it.setMaxZoomPreference(8.0)
            map = it
        }
        onDispose { owner.lifecycle.removeObserver(observer); view.removeOnDidFailLoadingMapListener(failure); stop(); view.onDestroy() }
    }
    // Native Android 11.x repeats world tiles. Keep the entire north-up viewport
    // inside one world, rather than only constraining the camera's centre.
    DisposableEffect(map, view) {
        val current = map
        var applying = false
        var lastLimit = Double.NaN
        fun constrainViewport() {
            if (current == null || view.width <= 0 || applying) return
            applying = true
            try {
                val widthDp = view.width / context.resources.displayMetrics.density.toDouble()
                val minimum = QiblaMapViewport.minimumZoom(widthDp)
                if (kotlin.math.abs(current.minZoomLevel - minimum) > .000001) {
                    current.setMinZoomPreference(minimum)
                }
                val limit = QiblaMapViewport.longitudeLimit(widthDp, current.cameraPosition.zoom.coerceAtLeast(minimum))
                if (limit != lastLimit) {
                    lastLimit = limit
                    current.setLatLngBoundsForCameraTarget(LatLngBounds.from(85.051128, limit, -85.051128, -limit))
                }
            } finally { applying = false }
        }
        val cameraListener = MapLibreMap.OnCameraMoveListener { constrainViewport() }
        val layoutListener = android.view.View.OnLayoutChangeListener { _, _, _, _, _, _, _, _, _ -> constrainViewport() }
        current?.addOnCameraMoveListener(cameraListener)
        view.addOnLayoutChangeListener(layoutListener)
        constrainViewport()
        onDispose {
            current?.removeOnCameraMoveListener(cameraListener)
            view.removeOnLayoutChangeListener(layoutListener)
        }
    }
    LaunchedEffect(map, dark) {
        val current = map ?: return@LaunchedEffect
        styleReady = false
        // A bundled Natural Earth map is always available without a connection.
        val water = if (dark) "#0F1D2B" else "#F5F2EA"
        val land = if (dark) "#253747" else "#E1DBCE"
        val builder = Style.Builder().fromJson("""{"version":8,"sources":{},"layers":[{"id":"water","type":"background","paint":{"background-color":"$water"}}]}""")
            .withSource(GeoJsonSource("world", URI("asset://qibla_world.geojson")))
            .withLayer(FillLayer("land", "world").withProperties(fillColor(land), fillOutlineColor(if (dark) "#435566" else "#C7BEAC")))
        current.setStyle(builder) { style ->
            style.addImage("kaaba-label", mapLabel("Kaaba · Makkah", dark, context.resources.displayMetrics.density))
            style.addImage("user-label", mapLabel("You", dark, context.resources.displayMetrics.density))
            style.addSource(GeoJsonSource("qibla-route"))
            style.addLayer(LineLayer("qibla-route-line", "qibla-route").withProperties(lineColor(accent.toArgb()), lineWidth(2.5f), lineDasharray(arrayOf(2f, 2.2f)), lineCap("round")))
            style.addSource(GeoJsonSource("kaaba", Feature.fromGeometry(Point.fromLngLat(QiblaMath.kaaba.longitude, QiblaMath.kaaba.latitude))))
            style.addLayer(CircleLayer("kaaba-marker", "kaaba").withProperties(circleColor("#C69C52"), circleRadius(10f), circleStrokeColor("#FFFFFF"), circleStrokeWidth(3f)))
            style.addSource(GeoJsonSource("user"))
            style.addLayer(CircleLayer("user-marker", "user").withProperties(circleColor(accent.toArgb()), circleRadius(8f), circleStrokeColor("#FFFFFF"), circleStrokeWidth(3f)))
            style.addLayer(SymbolLayer("kaaba-name", "kaaba").withProperties(iconImage("kaaba-label"), iconAnchor("top"), iconOffset(arrayOf(0f, 16f)), iconAllowOverlap(true)))
            style.addLayer(SymbolLayer("user-name", "user").withProperties(iconImage("user-label"), iconAnchor("top"), iconOffset(arrayOf(0f, 14f)), iconAllowOverlap(true)))
            styleReady = true
        }
    }
    LaunchedEffect(map, styleReady, location, recenter) {
        val current = map ?: return@LaunchedEffect
        if (!styleReady) return@LaunchedEffect
        val style = current.style ?: return@LaunchedEffect
        val p = location
        style.getSourceAs<GeoJsonSource>("user")?.setGeoJson(if (p == null) FeatureCollection.fromFeatures(emptyArray<Feature>()) else FeatureCollection.fromFeatures(arrayOf(Feature.fromGeometry(Point.fromLngLat(p.longitude, p.latitude)))))
        // Split at the antimeridian so the line never crosses the whole map spuriously.
        val segments = mutableListOf<MutableList<Point>>()
        p?.let { QiblaMath.path(it).forEach { v ->
            val point = Point.fromLngLat(v.longitude, v.latitude)
            if (segments.isEmpty() || kotlin.math.abs(segments.last().last().longitude() - v.longitude) > 180) segments.add(mutableListOf())
            segments.last().add(point)
        } }
        style.getSourceAs<GeoJsonSource>("qibla-route")?.setGeoJson(FeatureCollection.fromFeatures(segments.filter { it.size > 1 }.map { Feature.fromGeometry(LineString.fromLngLats(it)) }))
        val target = LatLng(QiblaMath.kaaba.latitude, QiblaMath.kaaba.longitude)
        if (p == null || QiblaMath.distanceKm(p) < 1) current.moveCamera(CameraUpdateFactory.newLatLngZoom(target, if (p == null) 2.0 else 8.0))
        else {
            // Camera padding is in physical pixels; reserve space for complete marker labels
            // and floating controls instead of clipping Makkah at the edge.
            val density = context.resources.displayMetrics.density
            val bounds = LatLngBounds.Builder().include(target).include(LatLng(p.latitude, p.longitude))
            if (segments.size == 1) QiblaMath.path(p).forEach { bounds.include(LatLng(it.latitude, it.longitude)) }
            current.easeCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(),
                (76 * density).toInt(), (104 * density).toInt(),
                (76 * density).toInt(), (88 * density).toInt()), 700)
        }
    }
    var origin by remember { mutableStateOf<Offset?>(null) }
    DisposableEffect(map, styleReady, location) {
        val current = map
        fun updateOrigin() {
            origin = if (current != null && styleReady && location != null) {
                val point = current.projection.toScreenLocation(LatLng(location.latitude, location.longitude))
                if (point.x.isFinite() && point.y.isFinite()) Offset(point.x, point.y) else null
            } else null
        }
        val listener = MapLibreMap.OnCameraMoveListener { updateOrigin() }
        val layoutListener = android.view.View.OnLayoutChangeListener { _, _, _, _, _, _, _, _, _ -> updateOrigin() }
        current?.addOnCameraMoveListener(listener)
        view.addOnLayoutChangeListener(layoutListener)
        updateOrigin()
        onDispose {
            current?.removeOnCameraMoveListener(listener)
            view.removeOnLayoutChangeListener(layoutListener)
        }
    }
    val color by animateColorAsState(arrowColor, tween(320), label = "Map heading alignment")
    Box(modifier) {
        AndroidView(factory = { view }, modifier = Modifier.matchParentSize())
        // This drawing overlay has no pointer handler: map pan/pinch gestures still pass through.
        Canvas(Modifier.matchParentSize()) {
            val start = origin
            if (heading != null && start != null && start.x in 0f..size.width && start.y in 0f..size.height) {
                // Approximate physical 0.5 cm, measured from the dot centre to the tip.
                // The map is north-up, so true heading rotates clockwise directly in screen space.
                val length = (0.5f / 2.54f) * 160f * density
                val dotRadius = 11f * density
                val headLength = 12f * density
                val halfWidth = 6f * density
                // The sensor value is already smoothed; drawing it directly
                // keeps this marker synchronized with the compass and card.
                rotate(heading.toFloat(), start) {
                    val tip = Offset(start.x, start.y - length)
                    drawLine(color.copy(alpha = .15f), Offset(start.x, start.y - dotRadius),
                        Offset(start.x, tip.y + headLength), 7f * density, StrokeCap.Round)
                    drawLine(color, Offset(start.x, start.y - dotRadius),
                        Offset(start.x, tip.y + headLength), 3f * density, StrokeCap.Round)
                    drawPath(Path().apply {
                        moveTo(tip.x, tip.y)
                        lineTo(tip.x + halfWidth, tip.y + headLength)
                        lineTo(tip.x - halfWidth, tip.y + headLength)
                        close()
                    }, color)
                }
            }
        }
    }
}

// Bitmap labels keep the map completely independent of remote glyph/font servers.
private fun mapLabel(text: String, dark: Boolean, density: Float): android.graphics.Bitmap {
    val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 11f * density
        typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
    }
    val width = (paint.measureText(text) + 18f * density).toInt()
    val height = (24f * density).toInt()
    return android.graphics.Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.ARGB_8888).also { bitmap ->
        bitmap.density = (160 * density).toInt()
        val canvas = android.graphics.Canvas(bitmap)
        paint.color = android.graphics.Color.parseColor(if (dark) "#172938" else "#FFFFFF")
        canvas.drawRoundRect(0f, 0f, width.toFloat(), height.toFloat(), 8f * density, 8f * density, paint)
        paint.color = android.graphics.Color.parseColor(if (dark) "#F4F6F8" else "#21372D")
        canvas.drawText(text, 9f * density, height / 2f - (paint.ascent() + paint.descent()) / 2f, paint)
    }
}
