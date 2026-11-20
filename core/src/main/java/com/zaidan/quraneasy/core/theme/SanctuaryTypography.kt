package com.zaidan.quraneasy.core.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.ExperimentalTextApi
import com.zaidan.quraneasy.core.R

@OptIn(ExperimentalTextApi::class)
val SanctuaryUiFont = FontFamily(
    Font(R.font.plus_jakarta_sans, FontWeight.Normal, variationSettings = FontVariation.Settings(FontVariation.weight(400))),
    Font(R.font.plus_jakarta_sans, FontWeight.Medium, variationSettings = FontVariation.Settings(FontVariation.weight(500))),
    Font(R.font.plus_jakarta_sans, FontWeight.SemiBold, variationSettings = FontVariation.Settings(FontVariation.weight(600))),
    Font(R.font.plus_jakarta_sans, FontWeight.Bold, variationSettings = FontVariation.Settings(FontVariation.weight(700))),
)

private val defaults = Typography()
val SanctuaryTypography = Typography(
    displayLarge = defaults.displayLarge.copy(fontFamily = SanctuaryUiFont),
    displayMedium = defaults.displayMedium.copy(fontFamily = SanctuaryUiFont),
    displaySmall = defaults.displaySmall.copy(fontFamily = SanctuaryUiFont),
    headlineLarge = defaults.headlineLarge.copy(fontFamily = SanctuaryUiFont),
    headlineMedium = defaults.headlineMedium.copy(fontFamily = SanctuaryUiFont),
    headlineSmall = defaults.headlineSmall.copy(fontFamily = SanctuaryUiFont),
    titleLarge = defaults.titleLarge.copy(fontFamily = SanctuaryUiFont),
    titleMedium = defaults.titleMedium.copy(fontFamily = SanctuaryUiFont),
    titleSmall = defaults.titleSmall.copy(fontFamily = SanctuaryUiFont),
    bodyLarge = defaults.bodyLarge.copy(fontFamily = SanctuaryUiFont),
    bodyMedium = defaults.bodyMedium.copy(fontFamily = SanctuaryUiFont),
    bodySmall = defaults.bodySmall.copy(fontFamily = SanctuaryUiFont),
    labelLarge = defaults.labelLarge.copy(fontFamily = SanctuaryUiFont),
    labelMedium = defaults.labelMedium.copy(fontFamily = SanctuaryUiFont),
    labelSmall = defaults.labelSmall.copy(fontFamily = SanctuaryUiFont),
)
