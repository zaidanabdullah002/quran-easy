package com.zaidan.quraneasy.feature.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(showBackground = true)
@Composable
fun HomeFooter() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            // 1. Adds the safety padding for the system navigation bar (pill/buttons)
            .navigationBarsPadding()
            // 2. The 8dp height you requested
            .height(8.dp)
            // 3. Keep it transparent or match your AppBackground
            .background(Color.Transparent)
    )
}