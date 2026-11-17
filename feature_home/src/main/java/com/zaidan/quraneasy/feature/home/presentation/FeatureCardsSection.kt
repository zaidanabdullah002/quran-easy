package com.zaidan.quraneasy.feature.home.presentation

import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.CalendarViewWeek
import androidx.compose.material.icons.outlined.Gesture
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaidan.quraneasy.core.ui.QuranEasyActionSurface

private val featureCardBetweenGap = 14.dp
private val titleFontSize = 16.sp
private val subtitleFontSize = 12.sp
private val iconSize = 48.dp
private val cardPadding = 16.dp

@Composable
fun FeatureCardsSection(
    onReadQuranClick: () -> Unit,
    onTasbihClick: () -> Unit,
    onManzilClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        FeatureCard(
            title = "Read Quran",
            subtitle = "Browse by Surah, Juz",
            icon = Icons.Outlined.AutoStories,
            onClick = onReadQuranClick
        )
        Spacer(modifier = Modifier.height(featureCardBetweenGap))
        FeatureCard(
            title = "Tasbih",
            subtitle = "Digital counter for remembrance",
            icon = Icons.Outlined.Gesture,
            onClick = onTasbihClick
        )
        Spacer(modifier = Modifier.height(featureCardBetweenGap))
        FeatureCard(
            title = "Manzil",
            subtitle = "Selected verses for protection",
            icon = Icons.Outlined.CalendarViewWeek,
            onClick = onManzilClick
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FeatureCardsSectionPreview() {
    FeatureCardsSection(
        onReadQuranClick = {},
        onTasbihClick = {},
        onManzilClick = {}
    )
}

@Composable
private fun FeatureCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    QuranEasyActionSurface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(82.dp),
        shape = RoundedCornerShape(24.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(cardPadding)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BoxIcon(icon = icon)
            Spacer(modifier = Modifier.size(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = titleFontSize,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = subtitleFontSize,
                    fontWeight = FontWeight.SemiBold
                )
            }
            ShortcutArrow()
        }
    }
}

@Composable
private fun BoxIcon(icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Box(
        modifier = Modifier
            .size(iconSize)
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                shape = RoundedCornerShape(16.dp)
            )
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun ShortcutArrow() {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(16.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(16.dp)
        )
    }
}
