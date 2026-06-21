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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Gesture
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
import com.zaidan.quraneasy.core.ui.hapticClick
import com.zaidan.quraneasy.core.theme.AppIconTile
import com.zaidan.quraneasy.core.theme.AppPrimaryGradientBottom
import com.zaidan.quraneasy.core.theme.AppPrimaryGradientTop

private val featureCardBetweenGap = 8.dp
private val titleFontSize = 20.sp
private val subtitleFontSize = 12.sp
private val iconSize = 40.dp
private val cardPadding = 18.dp

@Composable
fun FeatureCardsSection(
    onReadQuranClick: () -> Unit,
    onTasbihClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        FeatureCard(
            title = "Read Quran",
            subtitle = "Browse by Surah, Juz",
            icon = Icons.Outlined.AutoStories,
            backgroundBrush = Brush.verticalGradient(
                listOf(
                    AppPrimaryGradientTop,
                    AppPrimaryGradientBottom
                )
            ),
            onClick = onReadQuranClick
        )
        Spacer(modifier = Modifier.height(featureCardBetweenGap))
        FeatureCard(
            title = "Tasbih",
            subtitle = "Digital counter for remembrance",
            icon = Icons.Outlined.Gesture,
            backgroundBrush = Brush.verticalGradient(
                listOf(
                    AppPrimaryGradientTop,
                    Color(0xFF1B2744)
                )
            ),
            onClick = onTasbihClick
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FeatureCardsSectionPreview() {
    FeatureCardsSection(
        onReadQuranClick = {},
        onTasbihClick = {}
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
        onClick = hapticClick(onClick = onClick),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Row(
            modifier = Modifier
                .background(backgroundBrush)
                .wrapContentHeight()
                .padding(cardPadding)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BoxIcon(icon = icon)
            Spacer(modifier = Modifier.size(20.dp))
            Column {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = titleFontSize,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.92f),
                    fontSize = subtitleFontSize,
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
            .size(iconSize)
            .clip(RoundedCornerShape(20.dp))
            .background(AppIconTile),
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
