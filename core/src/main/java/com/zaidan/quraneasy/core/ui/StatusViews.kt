package com.zaidan.quraneasy.core.ui


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AppLoadingView(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.padding(horizontal = 32.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(92.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val stroke = 9.dp.toPx()
                val inset = stroke / 2
                val arcSize = Size(size.width - stroke, size.height - stroke)

                drawArc(
                    color = colors.outlineVariant,
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = Offset(inset, inset),
                    size = arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Round)
                )

                drawArc(
                    color = colors.primary,
                    startAngle = -90f,
                    sweepAngle = 120f,
                    useCenter = false,
                    topLeft = Offset(inset, inset),
                    size = arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Round)
                )
            }

            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(colors.surface, RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Q",
                    color = colors.primary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Text(
            text = title,
            color = colors.onSurface,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = subtitle,
            color = colors.onSurfaceVariant,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
    }
}

@Composable
fun AppErrorView(
    message: String,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    QuranEasySurface(
        modifier = modifier.padding(horizontal = 24.dp),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = 2.dp
    ) {
        Text(
            text = message,
            color = colors.onSurface,
            fontSize = 15.sp,
            textAlign = TextAlign.Center
        )
    }
}
