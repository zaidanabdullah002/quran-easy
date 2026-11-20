package com.zaidan.quraneasy.core.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.zaidan.quraneasy.core.settings.AppSettings
import com.zaidan.quraneasy.core.settings.QuranFontChoice

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
    val fontFamily = when (settings.quranFont) {
        QuranFontChoice.AL_QALAM -> IndoPakQuranFont
        QuranFontChoice.AMIRI -> AmiriQuranFont
    }
    return QuranTypography(
        fontFamily = fontFamily,
        ayahFontSize = 26.sp * scale,
        ayahLineHeight = 54.sp * scale,
        verseMarkerFontSize = 18.sp * scale,
        bismillahFontSize = 28.sp * scale,
        bismillahLineHeight = 40.sp * scale,
    )
}

val LocalQuranTypography = staticCompositionLocalOf { quranTypography(AppSettings()) }

