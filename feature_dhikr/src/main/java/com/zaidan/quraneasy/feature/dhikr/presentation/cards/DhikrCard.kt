package com.zaidan.quraneasy.feature.dhikr.presentation.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaidan.quraneasy.feature.dhikr.presentation.DhikrItem

private val NeutralText = Color(0xFF1D1D1F)
private val NeutralSubtext = Color(0xFF747C8C)
private val SurfaceWhite = Color.White
private val SurfaceBorder = Color(0xFFD9D9DE)
private val SoftRow = Color(0xFFF4F4F6)

@Preview(showBackground = true)
@Composable
private fun DhikrCardPreview() {
    DhikrCard(
        item = DhikrItem(
            countLabel = "100x",
            reference = "Sahih Muslim 2691",
            arabic = "سُبْحَانَ اللهِ وَبِحَمْدِهِ",
            transliteration = "Subhanallahi wa bihamdihi",
            translation = "Glory be to Allah and praise Him"
        )
    )
}

@Composable
fun DhikrCard(
    item: DhikrItem
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, SurfaceBorder)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(SoftRow)
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = item.countLabel,
                        color = NeutralText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = item.reference,
                    color = NeutralSubtext,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .border(3.dp, SurfaceBorder, CircleShape)
                )
            }

            Text(
                text = item.arabic,
                color = NeutralText,
                fontSize = 28.sp,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = item.transliteration,
                color = NeutralSubtext,
                fontSize = 20.sp,
                fontStyle = FontStyle.Italic
            )

            Text(
                text = item.translation,
                color = NeutralText,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
