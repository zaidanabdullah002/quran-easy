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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
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
    val prayerCompleted = remember { mutableStateListOf(false,false,false,false,false) }
    val totalPrayerCount = remember { mutableIntStateOf(0) }
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
                        text = "${totalPrayerCount.intValue} of 5 completed",
                        color = SecondaryText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Box(
                    modifier = Modifier
                        .size(104.dp)
                        .clip(CircleShape)
                        .border(8.dp, Color(0xFFF2F2F4), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${totalPrayerCount.intValue}/5",
                        color = PrimaryText,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            PrayerRow("Fajr", "05:30",prayerCompleted[0],0,
                onClick = {index -> handlePrayerClick(index,prayerCompleted,totalPrayerCount)})
            Spacer(modifier = Modifier.height(14.dp))
            PrayerRow("Dhuhr", "12:45",prayerCompleted[1],1,
                onClick = {index -> handlePrayerClick(index,prayerCompleted,totalPrayerCount)})
            Spacer(modifier = Modifier.height(14.dp))
            PrayerRow("Asr", "16:15",prayerCompleted[2],2,
                onClick = {index -> handlePrayerClick(index,prayerCompleted,totalPrayerCount)})
            Spacer(modifier = Modifier.height(14.dp))
            PrayerRow("Maghrib", "18:56",prayerCompleted[3],3,
                onClick = {index -> handlePrayerClick(index,prayerCompleted,totalPrayerCount)})
            Spacer(modifier = Modifier.height(14.dp))
            PrayerRow("Isha", "20:10",prayerCompleted[4],4,
                onClick = {index -> handlePrayerClick(index,prayerCompleted,totalPrayerCount)})
        }
    }


}

private fun handlePrayerClick(
    index: Int,
    prayerCompleted: SnapshotStateList<Boolean>,
    totalPrayerCount: MutableIntState
){
    prayerCompleted[index] = !prayerCompleted[index]
    totalPrayerCount.intValue = prayerCompleted.count { it }

    println("Prayer clicked $index")

}

@Composable
private fun PrayerRow(name: String, time: String,completed: Boolean,pos: Int,onClick: (Int) -> Unit) {

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
            if(!completed){
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(3.dp, Color(0xFFC9CDD6), CircleShape)
                        .clickable(
                            onClick = { onClick(pos) }
                        )
                )
            }else{
                Image(
                    painter = painterResource(id = R.drawable.check_tick),
                    contentDescription = "Tick",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(3.dp, Color(0xFFC9CDD6), CircleShape)
                        .clickable(
                            onClick = { onClick(pos) }
                        )
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
