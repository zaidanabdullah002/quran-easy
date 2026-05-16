package com.zaidan.quraneasy.feature.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Gesture
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FeatureCardsSection(
    onReadQuranClick: () -> Unit,
    onTasbihClick: () -> Unit,
    onDhikrClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        FeatureCard(
            title = "Read Quran",
            subtitle = "Browse by Surah, Juz or Bookmarks",
            icon = Icons.Outlined.AutoStories,
            backgroundBrush = Brush.verticalGradient(listOf(CardDarkTop, CardDarkBottom)),
            onClick = onReadQuranClick
        )
        Spacer(modifier = Modifier.height(18.dp))
        FeatureCard(
            title = "Tasbih",
            subtitle = "Digital counter for dhikr",
            icon = Icons.Outlined.Gesture,
            backgroundBrush = Brush.verticalGradient(listOf(CardDarkTop, Color(0xFF1B2744))),
            onClick = onTasbihClick
        )
        Spacer(modifier = Modifier.height(18.dp))
        FeatureCard(
            title = "Daily Dhikr",
            subtitle = "Morning & evening remembrance",
            icon = Icons.Outlined.StarOutline,
            backgroundBrush = Brush.verticalGradient(listOf(CardCharcoalTop, CardCharcoalBottom)),
            onClick = onDhikrClick
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FeatureCardsSectionPreview() {
    FeatureCardsSection(
        onReadQuranClick = {},
        onTasbihClick = {},
        onDhikrClick = {}
    )
}

@Composable
private fun FeatureCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    backgroundBrush: Brush,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Row(
            modifier = Modifier
                .background(backgroundBrush)
                .padding(horizontal = 22.dp, vertical = 26.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BoxIcon(icon = icon)
            Spacer(modifier = Modifier.size(20.dp))
            Column {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.92f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun BoxIcon(icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(IconTile),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}
