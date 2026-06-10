package com.zaidan.quraneasy.feature.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaidan.quraneasy.core.theme.AppPrimaryGradientBottom
import com.zaidan.quraneasy.core.theme.AppPrimaryGradientTop

@Preview(showBackground = true)
@Composable
fun HomeHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(AppPrimaryGradientTop, AppPrimaryGradientBottom)
                ),
                shape = RoundedCornerShape(bottomStart = 0.dp, bottomEnd = 0.dp)
            )
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 16.dp, top = 16.dp, bottom = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 2. Stacked Typography: Creates better visual hierarchy
            Column {
                Text(
                    text = "Al Quran",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "القرآن الكريم",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            // 3. Modern Icon Buttons: Uses subtle backgrounds for depth
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = { /* TODO */ },
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.Stars,
                        contentDescription = "Premium",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(onClick = { /* TODO */ }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Menu",
                        tint = Color.White
                    )
                }
            }
        }
    }
}
