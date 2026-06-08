package com.zaidan.quraneasy.feature.tasbih.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaidan.quraneasy.core.theme.AppDimens

@Preview(showBackground = true)
@Composable
fun TasbihContentPreview() {
    TasbihContent(
        uiState = TasbihUiState(),
        onBackClick = {},
        onResetClick = {},
        onSwipeUp = {},
        onTargetChange = {}
    )
}

@SuppressLint("UnusedBoxWithConstraintsScope")
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
        modifier = Modifier
            .fillMaxSize()


    ) {

        TasbihTopBar(
            onBackClick = onBackClick,
            onResetClick = onResetClick
        )

        TasbihSwipeArea(
            onSwipeUp = onSwipeUp,
            modifier = Modifier.fillMaxSize()
        ) {

            BoxWithConstraints(
                modifier = Modifier.fillMaxSize()
            ) {

                /*
                 * Responsive breakpoints
                 */
                val isCompact = maxHeight < 700.dp
                val isMedium = maxHeight in 700.dp..820.dp

                /*
                 * Vertical spacing zones
                 */
                val topZone = when {
                    isCompact -> 170.dp
                    isMedium -> 190.dp
                    else -> 210.dp
                }

                val bottomZone = when {
                    isCompact -> 220.dp
                    isMedium -> 240.dp
                    else -> 260.dp
                }

                /*
                 * Available space for ring
                 */
                val availableRingHeight =
                    (maxHeight - topZone - bottomZone)
                        .coerceAtLeast(220.dp)

                val availableRingWidth =
                    (maxWidth - 56.dp)
                        .coerceAtLeast(220.dp)

                /*
                 * Final responsive ring size
                 * Added upper limit for tablets/foldables
                 */
                val ringSize = minOf(
                    availableRingHeight,
                    availableRingWidth,
                    420.dp
                )

                /*
                 * Responsive typography
                 */
                val countTextSize = when {
                    isCompact -> 64.sp
                    isMedium -> 78.sp
                    else -> 92.sp
                }

                val labelTextSize = when {
                    isCompact -> 20.sp
                    isMedium -> 22.sp
                    else -> 24.sp
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            horizontal = AppDimens.ScreenPaddingLarge.dp
                        ),
                    contentAlignment = Alignment.TopCenter
                ) {

                    /*
                     * Counter ring
                     */
                    TasbihCounterRing(
                        count = uiState.count,
                        target = uiState.target,
                        modifier = Modifier
                            .padding(
                                top = topZone +
                                        ((availableRingHeight - ringSize) / 2)
                            )
                            .size(ringSize),
                        countTextSize = countTextSize,
                        labelTextSize = labelTextSize
                    )

                    /*
                     * Top content
                     */
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(top = 10.dp)
                    ) {

                        TasbihTargetChip(
                            target = uiState.target,
                            onClick = {
                                showDialog = true
                            }
                        )

                        if (showDialog) {

                            TargetPickerDialog(
                                currentTarget = uiState.target,
                                onDismiss = {
                                    showDialog = false
                                },
                                onConfirm = {
                                    showDialog = false
                                    onTargetChange(it)
                                }
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        TasbihTextChip(
                            tasbihText = uiState.tasbihText
                        )
                    }
                }
            }
        }
    }
}