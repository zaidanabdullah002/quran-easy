package com.zaidan.quraneasy.core.feedbacks

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.zaidan.quraneasy.core.controllers.PrayerTickFeedbackController

@Preview(showBackground = true)
@Composable
fun rememberPrayerTickFeedbackController(): PrayerTickFeedbackController {
    val context = LocalContext.current
    val controller = remember(context) {
        PrayerTickFeedbackControllerImpl(
            haptics = PrayerTickHaptics(context)
        )
    }

    DisposableEffect(controller) {
        onDispose {
            controller.release()
        }
    }

    return controller
}
