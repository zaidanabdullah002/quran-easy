package com.zaidan.quraneasy.feature.tasbih.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaidan.quraneasy.feature.tasbih.presentation.viewmodel.TasbihViewModel

@Preview(showBackground = true)
@Composable
fun TasbihScreenPreview() {
    TasbihScreen(onBackClick = {},viewModel = viewModel())
}

@Composable
fun TasbihScreen(
    onBackClick: () -> Unit,
    viewModel: TasbihViewModel
) {

    val uiState by viewModel.uiStateFlow.collectAsState()

    TasbihContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onResetClick = { viewModel.resetCount() },
        onSwipeUp = { viewModel.incrementCount() },
        onTargetChange = {
            viewModel.setTarget(it)
        }
    )
}
