package com.zaidan.quraneasy.feature.tasbih.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zaidan.quraneasy.core.theme.AppDimens

@Preview(showBackground = true)
@Composable
fun TasbihContentPreview(){
    TasbihContent(
        uiState = TasbihUiState(),
        onBackClick = {},
        onResetClick = {},
        onSwipeUp = {},
        onTargetChange = {}
    )
}

@Composable
fun TasbihContent(
    uiState: TasbihUiState,
    onBackClick: () -> Unit,
    onResetClick: () -> Unit,
    onSwipeUp: () -> Unit,
    onTargetChange: (Int) -> Unit
) {
    var showDialog by remember {
        mutableStateOf(false)
    }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TasbihTopBar(
            onBackClick = onBackClick,
            onResetClick = onResetClick
        )

        TasbihSwipeArea(
            onSwipeUp = onSwipeUp,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = AppDimens.ScreenPaddingLarge.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                // The Counter Ring is placed first and pushed towards the top
                TasbihCounterRing(
                    count = uiState.count,
                    target = uiState.target,
                    modifier = Modifier.padding(top = 180.dp)
                )

                // Chips overlap the top area of the ring
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 10.dp)
                ) {
                    TasbihTargetChip(
                        target = uiState.target,
                        onClick = { showDialog = true }
                    )

                    if (showDialog) {
                        TargetPickerDialog(
                            currentTarget = uiState.target,
                            onDismiss = { showDialog = false },
                            onConfirm = {
                                showDialog = false
                                onTargetChange(it)
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    TasbihTextChip(tasbihText = uiState.tasbihText)
                }
            }
        }
    }
}
