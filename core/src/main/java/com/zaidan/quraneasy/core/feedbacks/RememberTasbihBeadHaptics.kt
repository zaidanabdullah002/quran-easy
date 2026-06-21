package com.zaidan.quraneasy.core.feedbacks

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
fun rememberTasbihBeadHaptics(): TasbihBeadHaptics {
    val context = LocalContext.current
    return remember(context) { TasbihBeadHaptics(context) }
}
