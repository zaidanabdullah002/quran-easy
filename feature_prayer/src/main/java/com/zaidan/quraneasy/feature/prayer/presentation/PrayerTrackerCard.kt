package com.zaidan.quraneasy.feature.prayer.presentation

import androidx.compose.foundation.Image
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
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


private val SurfaceWhite = Color(0xFFFFFFFF)
private val PrimaryText = Color(0xFF20242C)
private val SecondaryText = Color(0xFF6C7382)
private val SoftRow = Color(0xFFF6F6F8)


@Preview(showBackground = true)
@Composable
fun PrayerTrackerCard() {
    val prayers = remember {
        mutableStateListOf(
            PrayerUiState("Fajr", "05:30"),
            PrayerUiState("Dhuhr", "12:45"),
            PrayerUiState("Asr", "16:15"),
            PrayerUiState("Maghrib", "18:56"),
            PrayerUiState("Isha", "20:10")
        )
    }
    val totalPrayerCount = prayers.count { it.completed }
    val progress = totalPrayerCount / 5f

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Prayer Tracker",
                        color = PrimaryText,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${totalPrayerCount} of 5 completed",
                        color = SecondaryText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Box(
                    modifier = Modifier
                        .size(104.dp)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.size(104.dp),
                        strokeWidth = 8.dp,
                        color = Color(0xFF20242C),
                        trackColor = Color(0xFFF2F2F4)
                    )
                    Text(
                        text = "${totalPrayerCount}/5",
                        color = PrimaryText,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            prayers.forEachIndexed { index, prayer ->
                PrayerRow(
                    name = prayer.name,
                    time = prayer.time,
                    completed = prayer.completed,
                    onClick = {
                        prayers[index] = prayer.copy(completed = !prayer.completed)
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
            .clip(RoundedCornerShape(14.dp))
            .background(SoftRow)
            .padding(horizontal = 12.dp, vertical = 12.dp),
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
                        .border(3.dp, Color(0xFFC9CDD6), CircleShape)
                        .clickable(onClick = onClick)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(3.dp, Color(0xFFC9CDD6), CircleShape)
                        .clickable(onClick = onClick)
                )
            }

            Spacer(modifier = Modifier.size(16.dp))
            Text(
                text = name,
                color = Color(0xFF5B606B),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = time,
            color = SecondaryText,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
