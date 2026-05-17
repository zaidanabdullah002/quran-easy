package com.zaidan.quraneasy.feature.dhikr.presentation.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaidan.quraneasy.core.theme.AppDimens

private val NeutralText = Color(0xFF1D1D1F)
private val NeutralSubtext = Color(0xFF747C8C)
private val ProgressTrack = Color(0xFFE9E9ED)

@Preview(showBackground = true)
@Composable
private fun DhikrProgressBarPreview() {
    DhikrProgressBar(completed = 2, total = 4)
}

@Composable
fun DhikrProgressBar(
    completed: Int,
    total: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .shadow(4.dp)
            .padding(horizontal = AppDimens.ScreenPadding.dp, vertical = AppDimens.ScreenPadding.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Progress",
                color = NeutralSubtext,
                fontSize = 20.sp
            )
            Text(
                text = "$completed / $total",
                color = NeutralText,
                fontSize = 20.sp
            )
        }

        Spacer(modifier = Modifier.size(AppDimens.ItemSpacing.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(AppDimens.RoundRadius.dp))
                .background(ProgressTrack)
        )
    }
}
