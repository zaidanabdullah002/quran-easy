package com.zaidan.quraneasy.feature.settings.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
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
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack, enabled = !state.saving) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back))
                    }
                }
            )
        },
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
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    SectionLabel(stringResource(R.string.appearance))
                    Column(Modifier.fillMaxWidth().heightIn(min = 56.dp).clickable(enabled) { themeDialog = true }
                        .padding(vertical = 12.dp)) {
                        Text(stringResource(R.string.theme), style = MaterialTheme.typography.titleMedium)
                        Text(themeLabel(settings.themeMode), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    HorizontalDivider()
                    SectionLabel(stringResource(R.string.reading))
                    Column(Modifier.padding(vertical = 8.dp)) {
                        Text(stringResource(R.string.font), style = MaterialTheme.typography.titleMedium)
                        Text(stringResource(R.string.included_font), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(sliderLabel, modifier = Modifier.weight(1f))
                        Text(stringResource(R.string.percent, (previewScale * 100).roundToInt()))
                    }
                    Slider(
                        value = previewScale,
                        onValueChange = { previewScale = it },
                        onValueChangeFinished = { viewModel.scale(previewScale) },
                        valueRange = MIN_QURAN_TEXT_SCALE..MAX_QURAN_TEXT_SCALE,
                        enabled = enabled,
                        modifier = Modifier.fillMaxWidth().semantics { contentDescription = sliderLabel },
                    )
                    Text(stringResource(R.string.preview), style = MaterialTheme.typography.labelMedium)
                    Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.medium) {
                        Text(
                            stringResource(R.string.arabic_preview),
                            style = TextStyle(textDirection = TextDirection.Rtl),
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            fontFamily = typography.fontFamily,
                            fontSize = typography.bismillahFontSize,
                            lineHeight = typography.bismillahLineHeight,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    SettingSwitch(stringResource(R.string.translated_names),
                        settings.showTranslatedSurahNames, enabled, viewModel::translatedNames)
                    SettingSwitch(stringResource(R.string.keep_awake),
                        settings.keepScreenAwakeWhileReading, enabled, viewModel::keepAwake)
                    TextButton(onClick = { resetDialog = true }, enabled = enabled) {
                        Text(stringResource(R.string.reset_reading))
                    }
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
    Text(label, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge)
}

@Composable
private fun SettingSwitch(label: String, value: Boolean, enabled: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().toggleable(value, enabled = enabled, role = Role.Switch, onValueChange = onChange)
            .heightIn(min = 56.dp).padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, Modifier.weight(1f).padding(end = 16.dp))
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
