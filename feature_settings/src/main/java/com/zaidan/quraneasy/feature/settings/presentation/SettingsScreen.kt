package com.zaidan.quraneasy.feature.settings.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.zaidan.quraneasy.core.navigation.Screen
import com.zaidan.quraneasy.core.settings.*
import com.zaidan.quraneasy.core.theme.quranTypography
import com.zaidan.quraneasy.core.ui.SanctuaryBanner
import com.zaidan.quraneasy.feature_settings.R
import kotlin.math.roundToInt

fun NavGraphBuilder.settingsGraph(navController: NavHostController) {
    composable(Screen.Settings.route) {
        val viewModel: SettingsViewModel = hiltViewModel()
        SettingsScreen(viewModel, onBack = { navController.popBackStack() })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel, onBack: () -> Unit) {
    val layoutDirection = LocalLayoutDirection.current
    val darkTheme = MaterialTheme.colorScheme.background.luminance() < 0.2f
    val sliderAccent = if (darkTheme) Color(0xFF06B6D4) else MaterialTheme.colorScheme.primary
    val sliderValueColor = if (darkTheme) Color(0xFF38BDF8) else MaterialTheme.colorScheme.secondary
    val state by viewModel.state.collectAsStateWithLifecycle()
    var themeDialog by rememberSaveable { mutableStateOf(false) }
    var resetDialog by rememberSaveable { mutableStateOf(false) }
    val snackbar = remember { SnackbarHostState() }
    val saveError = stringResource(R.string.save_error)
    val dismiss = stringResource(R.string.dismiss)
    LaunchedEffect(state.saveError) {
        if (state.saveError) {
            snackbar.showSnackbar(saveError, dismiss)
            viewModel.dismissError()
        }
    }
    // An accepted write finishes before navigation can clear this ViewModel.
    BackHandler(enabled = state.saving) { }
    Scaffold(
        contentWindowInsets = WindowInsets(0),
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        val settings = state.settings
        when {
            state.loading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            state.readError || settings == null -> Column(
                Modifier.fillMaxSize().padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(stringResource(R.string.read_error))
                TextButton(onClick = viewModel::reload) { Text(stringResource(R.string.retry)) }
            }
            else -> {
                var previewScale by remember {
                    mutableFloatStateOf(settings.quranTextScale)
                }
                LaunchedEffect(settings.quranTextScale, state.saving) {
                    if (!state.saving) previewScale = settings.quranTextScale
                }
                val enabled = !state.saving
                val typography = quranTypography(settings.copy(quranTextScale = previewScale))
                val sliderLabel = stringResource(R.string.text_size)
                Column(
                    Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    SanctuaryBanner(com.zaidan.quraneasy.core.R.drawable.sanctuary_reading, "SANCTUARY MODE",
                        "Personalize sacred script readability for dawn and night contemplations.")
                    SectionLabel(stringResource(R.string.appearance))
                    SettingsPanel(verticalPadding = 4.dp) {
                        Row(Modifier.fillMaxWidth().heightIn(min = 48.dp).clickable(enabled) { themeDialog = true },
                            verticalAlignment = Alignment.CenterVertically) {
                            Text(stringResource(R.string.theme), Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
                            Text(themeLabel(settings.themeMode), color = MaterialTheme.colorScheme.secondary)
                        }
                    }
                    SectionLabel("Reading & Typography")
                    SettingsPanel {
                        Text(stringResource(R.string.included_font), style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold)
                        Text("Active default script · Optimized for reading", color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall)
                        Surface(color = MaterialTheme.colorScheme.surfaceContainerLow, shape = RoundedCornerShape(12.dp)) {
                            Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("LIVE PREVIEW", style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.secondary)
                                Text(
                                    stringResource(R.string.arabic_preview),
                                    style = TextStyle(textDirection = TextDirection.Rtl),
                                    modifier = Modifier.fillMaxWidth(),
                                    fontFamily = typography.fontFamily,
                                    fontSize = typography.bismillahFontSize,
                                    lineHeight = typography.bismillahLineHeight,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                            }
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(sliderLabel, modifier = Modifier.weight(1f))
                            Surface(shape = CircleShape, color = sliderAccent.copy(alpha = .15f),
                                border = BorderStroke(1.dp, sliderAccent.copy(alpha = .4f))) {
                                Text(stringResource(R.string.percent, (previewScale * 100).roundToInt()),
                                    Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold,
                                    color = sliderValueColor)
                            }
                        }
                        val trackColor = if (darkTheme) Color(0xFF1E293B)
                            else MaterialTheme.colorScheme.surfaceContainerHighest
                        Slider(
                            value = previewScale,
                            onValueChange = { previewScale = it },
                            onValueChangeFinished = { viewModel.scale(previewScale) },
                            valueRange = MIN_QURAN_TEXT_SCALE..MAX_QURAN_TEXT_SCALE,
                            enabled = enabled,
                            modifier = Modifier.fillMaxWidth().semantics { contentDescription = sliderLabel },
                            thumb = {
                                Box(Modifier.size(20.dp).background(
                                    sliderAccent.copy(alpha = if (enabled) 1f else .38f), CircleShape))
                            },
                            track = { sliderState ->
                                Canvas(Modifier.fillMaxWidth().height(8.dp)) {
                                    val fraction = ((sliderState.value - MIN_QURAN_TEXT_SCALE) /
                                        (MAX_QURAN_TEXT_SCALE - MIN_QURAN_TEXT_SCALE)).coerceIn(0f, 1f)
                                    val rtl = layoutDirection == LayoutDirection.Rtl
                                    val start = if (rtl) size.width else 0f
                                    val end = if (rtl) size.width * (1f - fraction) else size.width * fraction
                                    drawLine(trackColor, Offset(0f, center.y), Offset(size.width, center.y),
                                        strokeWidth = size.height, cap = StrokeCap.Round)
                                    if (fraction > 0f) drawLine(sliderAccent.copy(alpha = if (enabled) 1f else .38f),
                                        Offset(start, center.y), Offset(end, center.y),
                                        strokeWidth = size.height, cap = StrokeCap.Round)
                                }
                            },
                            colors = SliderDefaults.colors(thumbColor = sliderAccent,
                                activeTrackColor = sliderAccent,
                                inactiveTrackColor = if (darkTheme) Color(0xFF1E293B)
                                    else MaterialTheme.colorScheme.surfaceContainerHighest),
                        )
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically) {
                            Text("80% · Compact", style = MaterialTheme.typography.labelSmall)
                            TextButton(onClick = { previewScale = 1f; viewModel.scale(1f) }, enabled = enabled,
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)) {
                                Text("100% · Default", style = MaterialTheme.typography.labelSmall, color = sliderValueColor)
                            }
                            Text("140% · Grand", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    SectionLabel("Reading Preferences")
                    SettingsPanel {
                        SettingSwitch(stringResource(R.string.translated_names),
                            settings.showTranslatedSurahNames, enabled, viewModel::translatedNames,
                            "Displays English translations beneath Surah titles.")
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        SettingSwitch(stringResource(R.string.keep_awake),
                            settings.keepScreenAwakeWhileReading, enabled, viewModel::keepAwake,
                            "Prevents the display from turning off while reading Quran or Manzil.")
                    }
                    SettingsPanel {
                        Text("Reset Reading Defaults", fontWeight = FontWeight.SemiBold)
                        Text("Restore standard font scale and reading toggles",
                            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        FilledTonalButton(onClick = { resetDialog = true }, enabled = enabled, modifier = Modifier.fillMaxWidth()) {
                            Text(stringResource(R.string.reset_reading))
                        }
                    }
                    Text("Your saved bookmarks are preserved when reading settings are reset.",
                        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(8.dp))
                    if (state.saving) LinearProgressIndicator(Modifier.fillMaxWidth())
                }
            }
        }
    }
    if (themeDialog) {
        AlertDialog(
            onDismissRequest = { themeDialog = false },
            title = { Text(stringResource(R.string.theme)) },
            text = {
                Column(Modifier.selectableGroup()) {
                    ThemeMode.entries.forEach { mode ->
                        Row(
                            Modifier.fillMaxWidth().selectable(
                                selected = state.settings?.themeMode == mode,
                                enabled = !state.saving,
                                role = Role.RadioButton,
                                onClick = { themeDialog = false; viewModel.theme(mode) },
                            ).padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(selected = state.settings?.themeMode == mode, onClick = null)
                            Spacer(Modifier.width(12.dp))
                            Text(themeLabel(mode))
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { themeDialog = false }) { Text(stringResource(R.string.cancel)) }
            },
        )
    }
    if (resetDialog) {
        AlertDialog(
            onDismissRequest = { resetDialog = false },
            title = { Text(stringResource(R.string.reset_title)) },
            text = { Text(stringResource(R.string.reset_description)) },
            confirmButton = {
                TextButton(onClick = { resetDialog = false; viewModel.reset() }, enabled = !state.saving) {
                    Text(stringResource(R.string.reset))
                }
            },
            dismissButton = {
                TextButton(onClick = { resetDialog = false }) { Text(stringResource(R.string.cancel)) }
            },
        )
    }
}

@Composable
private fun SectionLabel(label: String) {
    Text(label, color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp, fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(top = 8.dp, bottom = 0.dp))
}

@Composable
private fun SettingSwitch(label: String, value: Boolean, enabled: Boolean, onChange: (Boolean) -> Unit, description: String) {
    Row(
        Modifier.fillMaxWidth().toggleable(value, enabled = enabled, role = Role.Switch, onValueChange = onChange)
            .heightIn(min = 56.dp).padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f).padding(end = 12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
            Text(description, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = value, onCheckedChange = null, enabled = enabled)
    }
}

@Composable
private fun themeLabel(mode: ThemeMode): String = stringResource(
    when (mode) {
        ThemeMode.SYSTEM -> R.string.system
        ThemeMode.LIGHT -> R.string.light
        ThemeMode.DARK -> R.string.dark
    }
)

@Composable
private fun SettingsPanel(verticalPadding: androidx.compose.ui.unit.Dp = 16.dp, content: @Composable ColumnScope.() -> Unit) {
    Surface(border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        color = MaterialTheme.colorScheme.surfaceContainerLowest, shape = RoundedCornerShape(20.dp)) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = verticalPadding), verticalArrangement = Arrangement.spacedBy(10.dp), content = content)
    }
}
