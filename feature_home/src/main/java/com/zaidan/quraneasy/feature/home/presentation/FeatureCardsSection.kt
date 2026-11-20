package com.zaidan.quraneasy.feature.home.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.CalendarViewWeek
import androidx.compose.material.icons.outlined.Gesture
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaidan.quraneasy.core.ui.QuranEasyActionSurface

private val featureCardGap = 10.dp

@Composable
fun FeatureCardsSection(
    onReadQuranClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        FeatureCard(
            title = "Read Quran",
            subtitle = "Browse by Surah, Juz & Ayah",
            icon = Icons.Outlined.AutoStories,
            iconContainer = SanctuaryColors.ForestSoft,
            iconTint = SanctuaryColors.Forest,
            onClick = onReadQuranClick
        )
    }
}

@Composable
fun QuickFeatureCardsSection(
    onTasbihClick: () -> Unit,
    onManzilClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(featureCardGap)) {
        CompactFeatureCard(
            title = "Digital Tasbih",
            subtitle = "Count dhikr",
            icon = Icons.Outlined.Gesture,
            iconContainer = SanctuaryColors.TerracottaSoft,
            iconTint = SanctuaryColors.Terracotta,
            onClick = onTasbihClick,
            modifier = Modifier.weight(1f),
        )
        CompactFeatureCard(
            title = "Daily Manzil",
            subtitle = "Protection duas",
            icon = Icons.Outlined.CalendarViewWeek,
            iconContainer = SanctuaryColors.GoldSoft,
            iconTint = SanctuaryColors.Gold,
            onClick = onManzilClick,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun CompactFeatureCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconContainer: Color,
    iconTint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    QuranEasyActionSurface(
        onClick = onClick,
        modifier = modifier.height(88.dp),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, SanctuaryColors.Outline),
        containerColor = SanctuaryColors.Surface,
        contentColor = SanctuaryColors.Text,
        contentPadding = PaddingValues(0.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(42.dp).clip(RoundedCornerShape(14.dp)).background(iconContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.size(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = SanctuaryColors.Text, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(subtitle, color = SanctuaryColors.MutedText, fontSize = 10.sp, lineHeight = 13.sp)
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = SanctuaryColors.MutedText,
                modifier = Modifier.size(12.dp),
            )
        }
    }
}

@Composable
private fun FeatureCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconContainer: Color,
    iconTint: Color,
    onClick: () -> Unit
) {
    QuranEasyActionSurface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(82.dp),
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.dp, SanctuaryColors.Outline),
        containerColor = SanctuaryColors.Surface,
        contentColor = SanctuaryColors.Text,
        contentPadding = PaddingValues(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(iconContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.size(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = SanctuaryColors.Text,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = subtitle,
                    color = SanctuaryColors.MutedText,
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .border(1.dp, SanctuaryColors.Outline, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = SanctuaryColors.MutedText,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FeatureCardsSectionPreview() {
    FeatureCardsSection({})
}
