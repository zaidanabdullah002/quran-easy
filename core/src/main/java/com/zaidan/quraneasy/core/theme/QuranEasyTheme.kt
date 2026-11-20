package com.zaidan.quraneasy.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.zaidan.quraneasy.core.settings.AppSettings
import com.zaidan.quraneasy.core.settings.ThemeMode

private val LightColors = lightColorScheme(
    primary = Color(0xFF1E2E24),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD4E7D8),
    onPrimaryContainer = Color(0xFF0F1F16),
    secondary = Color(0xFF97472E),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFDBD0),
    onSecondaryContainer = Color(0xFF793019),
    tertiary = Color(0xFF8C651C),
    tertiaryContainer = Color(0xFFFFDEAE),
    onTertiaryContainer = Color(0xFF604100),
    background = Color(0xFFFCF9F4),
    onBackground = Color(0xFF1C1C19),
    surface = Color(0xFFFCF9F4),
    onSurface = Color(0xFF1C1C19),
    surfaceVariant = Color(0xFFF0EDE9),
    onSurfaceVariant = Color(0xFF434844),
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF6F3EE),
    surfaceContainer = Color(0xFFF0EDE9),
    surfaceContainerHigh = Color(0xFFEBE8E3),
    surfaceContainerHighest = Color(0xFFE5E2DD),
    outline = Color(0xFF737873),
    outlineVariant = Color(0xFFE5DED2),
)

// Effective colors from the supplied Stitch HTML (including inline overrides).
// Home retains its own scoped palette in HomeTheme.
private val DarkColors = darkColorScheme(
    primary = Color(0xFF38BDF8), onPrimary = Color.Black,
    primaryContainer = Color(0xFF0B110E), onPrimaryContainer = Color(0xFF38BDF8),
    inversePrimary = Color(0xFF006687),
    secondary = Color(0xFF10B981), onSecondary = Color.Black,
    secondaryContainer = Color(0xFF08282C), onSecondaryContainer = Color(0xFF38BDF8),
    tertiary = Color(0xFFF2BE71), onTertiary = Color(0xFF442B00),
    tertiaryContainer = Color(0xFF332919), onTertiaryContainer = Color(0xFFF2BE71),
    background = Color.Black, onBackground = Color(0xFFF1EFEA),
    surface = Color.Black, onSurface = Color(0xFFF1EFEA),
    surfaceVariant = Color(0xFF14201A), onSurfaceVariant = Color(0xFF94A3B8),
    surfaceContainerLowest = Color(0xFF0B110E),
    surfaceContainerLow = Color(0xFF070B09),
    surfaceContainer = Color(0xFF121A16),
    surfaceContainerHigh = Color(0xFF17201B),
    surfaceContainerHighest = Color(0xFF222A24),
    outline = Color(0xFF2DD4BF).copy(alpha = .4f),
    outlineVariant = Color(0xFF2DD4BF).copy(alpha = .2f),
    error = Color(0xFFFFB4AB), onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A), onErrorContainer = Color(0xFFFFDAD6),
)

private fun screenColors(dark: Boolean, route: String): ColorScheme {
    if (!dark) return LightColors
    return when {
        route == "settings" -> DarkColors.copy(
            primary = Color(0xFF10B981), onPrimary = Color(0xFF065F46),
            secondary = Color(0xFF2DD4BF), onSecondary = Color.Black,
            surfaceContainerLowest = Color(0xFF0E1512),
            outlineVariant = Color(0xFF10B981).copy(alpha = .2f),
        )
        route.startsWith("feeling") -> DarkColors.copy(
            primary = Color(0xFF7BD0FF), secondary = Color(0xFF4EDEA3),
            secondaryContainer = Color(0xFF103227), onSecondaryContainer = Color(0xFF4EDEA3),
            surfaceContainerLowest = Color(0xFF0D1512),
            surfaceContainerLow = Color(0xFF070E0B),
            outlineVariant = Color(0xFF10B981).copy(alpha = .25f),
        )
        route.startsWith("manzil") -> DarkColors.copy(
            primary = Color(0xFFF2BE71), secondary = Color(0xFF2DD4BF),
            secondaryContainer = Color(0xFF0B120F), onSecondaryContainer = Color(0xFF2DD4BF),
            surfaceContainerLowest = Color(0xFF0B120F),
            onSurface = Color.White,
        )
        else -> DarkColors
    }
}

val LocalAppSettings = staticCompositionLocalOf { AppSettings() }

@Composable
fun QuranEasyTheme(
    settings: AppSettings,
    route: String = "home",
    content: @Composable () -> Unit,
) {
    val darkTheme = when (settings.themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    val colors = screenColors(darkTheme, route)
    ApplySystemBars(colors, darkTheme)
    CompositionLocalProvider(
        LocalAppSettings provides settings,
        LocalQuranTypography provides quranTypography(settings),
    ) {
        MaterialTheme(colorScheme = colors, typography = SanctuaryTypography, content = content)
    }
}

@Composable
private fun ApplySystemBars(colors: ColorScheme, darkTheme: Boolean) {
    val view = LocalView.current
    if (view.isInEditMode) return
    SideEffect {
        val window = (view.context as? android.app.Activity)?.window ?: return@SideEffect
        window.statusBarColor = colors.background.toArgb()
        window.navigationBarColor = colors.background.toArgb()
        WindowCompat.getInsetsController(window, view).apply {
            isAppearanceLightStatusBars = !darkTheme
            isAppearanceLightNavigationBars = !darkTheme
        }
    }
}
