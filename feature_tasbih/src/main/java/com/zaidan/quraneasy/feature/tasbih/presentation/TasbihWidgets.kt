package com.zaidan.quraneasy.feature.tasbih.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.zaidan.quraneasy.core.theme.AppDimens
import com.zaidan.quraneasy.core.R
import kotlin.math.roundToInt


@Composable
fun TasbihTargetChip(target: Int) {
    Card(
        shape = RoundedCornerShape(AppDimens.CardRadius.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        RowCentered(
            modifier = Modifier.padding(horizontal = AppDimens.ScreenPaddingLarge.dp, vertical = AppDimens.ScreenPadding.dp)
        ) {
            Text(
                text = "☝",
                color = Color(0xFF3F3F3F),
                fontSize = 20.sp
            )
            Text(
                text = "Target: $target",
                color = Color(0xFF70798A),
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 10.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TasbihCounterRingPreview(){
    TasbihCounterRing(count = 1)
}

@Composable
fun TasbihCounterRing(count: Int) {
    Box(
        modifier = Modifier.height(380.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = 12f
            val radius = (size.minDimension - stroke) / 2f
            drawCircle(
                color = Color(0xFFF3F3F3),
                radius = radius,
                center = center,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = stroke)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Count",
                color = Color(0xFF70798A),
                fontSize = 30.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = count.toString(),
                color = Color(0xFF232323),
                fontSize = 92.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun TasbihSwipeAreaPreview() {
    TasbihSwipeArea(onSwipeUp = {})
}
@Composable
fun TasbihSwipeArea(
    onSwipeUp: () -> Unit
) {
    var beadOffsetY by remember { mutableFloatStateOf(0f) }
    var hasReachedReleaseZone by remember { mutableStateOf(false) }
    val maxOffset = 220f
    val releaseThreshold = -180f
    val slowdownZone = 120f
    val beadSize = 120.dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
            .clip(RoundedCornerShape(AppDimens.CardRadius.dp))
            .background(Color(0xFFF7F7F7)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Swipe bead up to count",
            color = Color(0xFF70798A),
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .navigationBarsPadding()
                .padding(bottom = 24.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .height(beadSize)
                    .width(beadSize)
                    .offset { IntOffset(0, beadOffsetY.roundToInt()) }
                    .padding(bottom = 8.dp)
                    .pointerInput(Unit) {
                        detectVerticalDragGestures(
                            onVerticalDrag = { _, dragAmount ->
                                val next = beadOffsetY + dragAmount
                                val limited = next.coerceIn(-maxOffset, 0f)
                                beadOffsetY = if (limited <= -slowdownZone) {
                                    val resistance = 0.35f
                                    beadOffsetY + (dragAmount * resistance)
                                } else {
                                    limited
                                }.coerceIn(-maxOffset, 0f)

                                hasReachedReleaseZone = beadOffsetY <= releaseThreshold
                            },
                            onDragEnd = {
                                if (hasReachedReleaseZone) {
                                    onSwipeUp()
                                }
                                beadOffsetY = 0f
                                hasReachedReleaseZone = false
                            }
                        )
                },
                contentAlignment = Alignment.TopCenter
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bead_png),
                    contentDescription = "Tasbih bead",
                    modifier = Modifier
                        .height(beadSize)
                        .width(beadSize)
                )
            }
        }
    }
}


@Composable
fun RowCentered(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        content()
    }
}
