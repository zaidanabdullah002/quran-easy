package com.zaidan.quraneasy.feature.tasbih.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(showBackground = true)
@Composable
fun TasbihContentPreview(){
    TasbihContent(
        uiState = TasbihUiState(),
        onBackClick = {},
        onResetClick = {},
        onSwipeUp = {}
    )
}

@Composable
fun TasbihContent(
    uiState: TasbihUiState,
    onBackClick: () -> Unit,
    onResetClick: () -> Unit,
    onSwipeUp: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TasbihTopBar(
            onBackClick = onBackClick,
            onResetClick = onResetClick
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(28.dp)
            ) {
                TasbihTargetChip(target = uiState.target)
                TasbihCounterRing(count = uiState.count)
                TasbihSwipeArea(
                    onSwipeUp = onSwipeUp
                )
            }
        }
    }
}
