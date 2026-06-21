package com.zaidan.quraneasy.core.feedbacks

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import com.zaidan.quraneasy.core.controllers.PrayerTickFeedbackController
import com.zaidan.quraneasy.core.ui.AppHaptics

@Preview(showBackground = true)
@Composable
fun rememberPrayerTickFeedbackController(): PrayerTickFeedbackController {
    val context = LocalContext.current
    val view = LocalView.current
    val controller = remember(context, view) {
        PrayerTickFeedbackControllerImpl(
            appHaptics = AppHaptics(view),
            prayerCompletionHaptics = PrayerCompletionHaptics(context)
        )
    }

    DisposableEffect(controller) {
        onDispose {
            controller.release()
        }
    }

    return controller
}
