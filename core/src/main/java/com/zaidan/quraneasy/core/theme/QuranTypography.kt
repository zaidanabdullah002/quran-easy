package com.zaidan.quraneasy.core.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.zaidan.quraneasy.core.settings.AppSettings

data class QuranTypography(
    val fontFamily: FontFamily,
    val ayahFontSize: TextUnit,
    val ayahLineHeight: TextUnit,
    val verseMarkerFontSize: TextUnit,
    val bismillahFontSize: TextUnit,
    val bismillahLineHeight: TextUnit,
)

fun quranTypography(settings: AppSettings): QuranTypography {
    val scale = settings.quranTextScale
    return QuranTypography(
        fontFamily = QuranFont,
        ayahFontSize = 34.sp * scale,
        ayahLineHeight = 60.sp * scale,
        verseMarkerFontSize = 22.sp * scale,
        bismillahFontSize = 31.sp * scale,
        bismillahLineHeight = 46.sp * scale,
    )
}

val LocalQuranTypography = staticCompositionLocalOf { quranTypography(AppSettings()) }
