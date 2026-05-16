package com.zaidan.quraneasy.feature.tasbih.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.text.font.FontWeight
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

@Composable
fun TasbihCounterRing(count: Int) {
    Box(
        modifier = Modifier.height(420.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val stroke = 18f
            val radius = (size.minDimension - stroke) / 2f
            drawCircle(
                color = Color(0xFFF0F0F0),
                radius = radius,
                center = center,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = stroke)
            )

            val arcStart = -10f
            val arcSweep = 36f
            drawArc(
                color = Color(0xFF2E2E2E),
                startAngle = arcStart,
                sweepAngle = arcSweep,
                useCenter = false,
                topLeft = Offset(stroke / 2f, stroke / 2f),
                size = Size(size.width - stroke, size.height - stroke),
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = stroke,
                    cap = androidx.compose.ui.graphics.StrokeCap.Round
                )
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

@Composable
fun TasbihSwipeArea(
    onSwipeUp: () -> Unit
) {
    var beadOffsetY by remember { mutableFloatStateOf(0f) }
    var dragAccumulator by remember { mutableFloatStateOf(0f) }
    val maxOffset = 220f
    val threshold = -90f

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
            Box(
                modifier = Modifier
                    .offset { IntOffset(0, beadOffsetY.roundToInt()) }
                    .pointerInput(Unit) {
                        detectVerticalDragGestures(
                            onVerticalDrag = { _, dragAmount ->
                                val next = beadOffsetY + dragAmount
                                beadOffsetY = next.coerceIn(-maxOffset, 0f)
                                dragAccumulator += dragAmount
                                if (dragAccumulator <= threshold) {
                                    onSwipeUp()
                                    dragAccumulator = 0f
                                    beadOffsetY = 0f
                                }
                            },
                            onDragEnd = {
                                beadOffsetY = 0f
                                dragAccumulator = 0f
                            }
                        )
                    }
                    .clip(CircleShape)
                    .background(Color(0xFF3A3A3A))
                    .padding(horizontal = 34.dp, vertical = 18.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Bead",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
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
