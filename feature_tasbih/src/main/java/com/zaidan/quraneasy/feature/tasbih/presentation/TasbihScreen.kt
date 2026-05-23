package com.zaidan.quraneasy.feature.tasbih.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Preview(showBackground = true)
@Composable
fun TasbihScreenPreview() {
    TasbihScreen(onBackClick = {})
}

@Composable
fun TasbihScreen(
    onBackClick: () -> Unit
) {

    var uiState by remember { mutableStateOf(TasbihUiState()) }

    TasbihContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onResetClick = { uiState = uiState.copy(count = 0) },
        onSwipeUp = { uiState = uiState.copy(count = uiState.count + 1) },
        onTargetChange = {
            uiState = uiState.copy(target = it)
        }
    )
}
