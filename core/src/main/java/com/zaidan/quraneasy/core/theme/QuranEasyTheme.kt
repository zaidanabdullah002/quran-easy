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
    primary = Color(0xFF355E93),
    onPrimary = Color.White,
    background = Color(0xFFF6F6F5),
    onBackground = Color(0xFF1D1D1F),
    surface = Color.White,
    onSurface = Color(0xFF1D1D1F),
    surfaceVariant = Color(0xFFF4F4F6),
    onSurfaceVariant = Color(0xFF747C8C),
    outline = Color(0xFFE8E1D5),
    outlineVariant = Color(0xFFE8E1D5),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFA9C7FF),
    onPrimary = Color(0xFF0E315A),
    background = Color(0xFF121416),
    onBackground = Color(0xFFE5E2E8),
    surface = Color(0xFF1B1E22),
    onSurface = Color(0xFFE5E2E8),
    surfaceVariant = Color(0xFF2A2E34),
    onSurfaceVariant = Color(0xFFC5C7CE),
    outline = Color(0xFF90939A),
    outlineVariant = Color(0xFF40444B),
)

val LocalAppSettings = staticCompositionLocalOf { AppSettings() }

@Composable
fun QuranEasyTheme(
    settings: AppSettings,
    content: @Composable () -> Unit,
) {
    val darkTheme = when (settings.themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    val colors = if (darkTheme) DarkColors else LightColors
    ApplySystemBars(colors, darkTheme)
    CompositionLocalProvider(
        LocalAppSettings provides settings,
        LocalQuranTypography provides quranTypography(settings),
    ) {
        MaterialTheme(colorScheme = colors, content = content)
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
