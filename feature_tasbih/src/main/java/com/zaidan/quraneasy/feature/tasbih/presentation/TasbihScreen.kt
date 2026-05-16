package com.zaidan.quraneasy.feature.tasbih.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.mutableIntStateOf

@Preview(showBackground = true)
@Composable
fun TasbihScreenPreview() {
    TasbihScreen(onBackClick = {})
}

@Composable
fun TasbihScreen(
    onBackClick: () -> Unit
) {
    var count by rememberSaveable { mutableIntStateOf(0) }
    val uiState = TasbihUiState(count = count)

    TasbihContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onResetClick = { count = 0 },
        onSwipeUp = { count += 1 }
    )
}
