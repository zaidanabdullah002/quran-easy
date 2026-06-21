package com.zaidan.quraneasy.feature.tasbih.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaidan.quraneasy.core.ui.HapticIconButton

@Preview(showBackground = true)
@Composable
fun TasbihTopBarPreview() {
    TasbihTopBar(onBackClick = {}, onResetClick = {})
}


@Composable
fun TasbihTopBar(
    onBackClick: () -> Unit,
    onResetClick: () -> Unit
) {
    Surface(color = Color(0xFF2E2E2E)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF2E2E2E))
                .height(90.dp)
                .statusBarsPadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            HapticIconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
            ) {
                Text(
                    text = "Tasbih Counter",
                    color = Color.White,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Keep your remembrance easy",
                    color = Color.White.copy(alpha = 0.78f),
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            HapticIconButton(onClick = onResetClick) {
                Icon(
                    imageVector = Icons.Outlined.Refresh,
                    contentDescription = "Reset",
                    tint = Color.White
                )
            }
        }
    }
}
