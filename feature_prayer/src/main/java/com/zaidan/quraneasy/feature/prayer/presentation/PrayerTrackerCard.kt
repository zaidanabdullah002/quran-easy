package com.zaidan.quraneasy.feature.prayer.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaidan.quraneasy.core.R
import com.zaidan.quraneasy.core.theme.AppPrimaryText
import com.zaidan.quraneasy.core.theme.AppSecondaryText
import com.zaidan.quraneasy.core.theme.AppSoftSurface
import com.zaidan.quraneasy.core.theme.AppSurface
import com.zaidan.quraneasy.core.theme.AppSurfaceBorder
import com.zaidan.quraneasy.feature.prayer.domain.Prayer
import com.zaidan.quraneasy.feature.prayer.presentation.viewmodel.PrayerViewModel

private val prayerCardHorizontalPadding = 12.dp
private val prayerCardVerticalPadding = 8.dp
private val titleFontSize = 20.sp
private val subtitleFontSize = 14.sp

@Preview(showBackground = true)
@Composable
fun PrayerTrackerCardPreview() {
    PrayerTrackerCard(
        uiState = PrayerUiState(
            prayers = listOf(
                Prayer("Fajr", "05:00 AM", true),
                Prayer("Dhuhr", "12:00 PM", false),
                Prayer("Asr", "03:30 PM", false),
                Prayer("Maghrib", "06:00 PM", false),
                Prayer("Isha", "07:30 PM", false)
            )
        ),
        onTogglePrayer = {}
    )
}

@Composable
fun PrayerTrackerCard(prayerViewModel: PrayerViewModel) {
    val uiState by prayerViewModel.uiStateFlow.collectAsState()
    PrayerTrackerCard(
        uiState = uiState,
        onTogglePrayer = { index -> prayerViewModel.togglePrayer(index) }
    )
}

@Composable
fun PrayerTrackerCard(
    uiState: PrayerUiState,
    onTogglePrayer: (Int) -> Unit
) {
    val prayers = uiState.prayers
    val totalPrayerCount = prayers.count { it.completed }
    val progress = if (prayers.isNotEmpty()) totalPrayerCount / prayers.size.toFloat() else 0f

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = AppSurface),
        border = BorderStroke(1.dp, AppSurfaceBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = prayerCardHorizontalPadding,
                vertical = prayerCardVerticalPadding)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Prayer Tracker",
                        color = AppPrimaryText,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${totalPrayerCount} of ${prayers.size} completed",
                        color = AppSecondaryText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.size(96.dp),
                        strokeWidth = 8.dp,
                        color = AppPrimaryText,
                        trackColor = AppSoftSurface
                    )
                    Text(
                        text = "${totalPrayerCount}/${prayers.size}",
                        color = AppPrimaryText,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            prayers.forEachIndexed { index, prayer ->
                PrayerRow(
                    name = prayer.name,
                    time = prayer.time,
                    completed = prayer.completed,
                    onClick = {
                        onTogglePrayer(index)
                    }
                )
                if (index != prayers.lastIndex) {
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }
        }
    }
}

@Composable
private fun PrayerRow(
    name: String,
    time: String,
    completed: Boolean,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AppSoftSurface)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (completed) {
                Image(
                    painter = painterResource(id = R.drawable.check_tick),
                    contentDescription = "Tick",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color(0xFFC9CDD6), CircleShape)
                        .clickable(onClick = onClick)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color(0xFFC9CDD6), CircleShape)
                        .clickable(onClick = onClick)
                )
            }

            Spacer(modifier = Modifier.size(14.dp))
            Text(
                text = name,
                color = AppPrimaryText,
                fontSize = titleFontSize,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = time,
            color = AppSecondaryText,
            fontSize = subtitleFontSize,
            fontWeight = FontWeight.Bold
        )
    }
}
