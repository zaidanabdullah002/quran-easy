package com.zaidan.quraneasy.core.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.zaidan.quraneasy.core.R

val AmiriQuranFont = FontFamily(
    Font(R.font.amiri_regular, FontWeight.Normal),
    Font(R.font.amiri_bold, FontWeight.Bold)
)

val IndoPakQuranFont = FontFamily(
    Font(R.font.al_qalam_quran_majeed_regular, FontWeight.Normal)
)

val QuranFont = AmiriQuranFont
