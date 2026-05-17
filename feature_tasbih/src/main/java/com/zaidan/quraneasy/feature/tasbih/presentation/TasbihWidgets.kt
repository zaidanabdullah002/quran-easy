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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt


@Composable
fun TasbihTargetChip(target: Int) {
    Card(
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        RowCentered(
            modifier = Modifier.padding(horizontal = 22.dp, vertical = 16.dp)
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
        modifier = Modifier.height(420.dp),
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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(340.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0xFFF7F7F7)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Swipe here",
            color = Color(0xFF777777),
            fontSize = 24.sp,
            modifier = Modifier.padding(top = 40.dp)
        )

        Text(
            text = "Swipe bead up to count",
            color = Color(0xFF70798A),
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                modifier = Modifier
                    .offset { IntOffset(0, beadOffsetY.roundToInt()) }
                    .shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(999.dp),
                        clip = false
                    )
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
                    }
                    .clip(RoundedCornerShape(999.dp)),
                color = Color(0xFF3A3A3A),
                contentColor = Color.White
            ) {
                Text(
                    text = "Bead",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                    ,
                    modifier = Modifier.padding(horizontal = 32.dp, vertical = 18.dp)
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
