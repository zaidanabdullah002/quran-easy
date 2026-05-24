package com.zaidan.quraneasy.feature.tasbih.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.zaidan.quraneasy.core.theme.AppDimens
import com.zaidan.quraneasy.core.R
import kotlin.math.roundToInt


@Composable
fun TasbihTargetChip(target: Int,
                     onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(AppDimens.CardRadius.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        onClick = onClick
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
fun TasbihTextChipPreview(){
    TasbihTextChip(tasbihText = "subhan allah")
}

@Composable
fun TasbihTextChip(tasbihText: String){
    Card(
        shape = RoundedCornerShape(AppDimens.CardRadius.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8E8E8)),
        ) {
        Text(
            text = tasbihText,
            color = Color(0xFF70798A),
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(10.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TasbihCounterRingPreview(){
    TasbihCounterRing(count = 1, target = 33)
}

@Composable
fun TasbihCounterRing(
    count: Int,
    target: Int,
    modifier: Modifier = Modifier,
    countTextSize: androidx.compose.ui.unit.TextUnit = 92.sp,
    labelTextSize: androidx.compose.ui.unit.TextUnit = 24.sp
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 14.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2f

            // Background Circle (Placeholder)
            drawCircle(
                color = Color(0xFFF3F3F3),
                radius = radius,
                style = Stroke(width = strokeWidth)
            )

            // Progress Arc
            val progress = (count.toFloat() / target.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
            drawArc(
                color = Color.DarkGray,
                startAngle = -90f,
                sweepAngle = progress * 360f,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2f, radius * 2f),
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Butt
                )
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = count.toString(),
                color = Color(0xFF232323),
                fontSize = countTextSize,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Count",
                color = Color(0xFF70798A),
                fontSize = labelTextSize,
                fontWeight = FontWeight.Medium
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun TasbihSwipeAreaPreview() {
    TasbihSwipeArea(onSwipeUp = {}) {
        Text("Swipe anywhere")
    }
}

@Composable
fun TasbihSwipeArea(
    onSwipeUp: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var beadOffsetY by remember { mutableFloatStateOf(0f) }
    var hasReachedReleaseZone by remember { mutableStateOf(false) }
    val maxOffset = 300f
    val releaseThreshold = -220f
    val slowdownZone = 170f
    val beadSize = 96.dp

    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onVerticalDrag = { change, dragAmount ->
                        val next = beadOffsetY + dragAmount
                        val limited = next.coerceIn(-maxOffset, 0f)
                        beadOffsetY = if (limited <= -slowdownZone) {
                            val resistance = 0.4f
                            beadOffsetY + (dragAmount * resistance)
                        } else {
                            limited
                        }.coerceIn(-maxOffset, 0f)

                        hasReachedReleaseZone = beadOffsetY <= releaseThreshold
                        if (beadOffsetY != 0f) change.consume()
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
    ) {
        content()

        Text(
            text = "Swipe Bead up to count",
            color = Color(0xFF70798A).copy(alpha = if (beadOffsetY == 0f) 0.6f else 0.2f),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = beadSize + 20.dp)
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 20.dp)
                .height(340.dp)
                .width(beadSize),
            contentAlignment = Alignment.BottomCenter
        ) {
            Image(
                painter = painterResource(id = R.drawable.button_bead),
                contentDescription = "Tasbih bead",
                modifier = Modifier
                    .offset { IntOffset(0, beadOffsetY.roundToInt()) }
                    .size(beadSize)
            )
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
