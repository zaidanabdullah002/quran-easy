package com.zaidan.quraneasy.feature.feeling.presentation.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zaidan.quraneasy.core.theme.AppPrimaryText
import com.zaidan.quraneasy.core.theme.AppSecondaryText
import com.zaidan.quraneasy.core.theme.AppSurface
import com.zaidan.quraneasy.feature.feeling.presentation.Feeling
import com.zaidan.quraneasy.feature.feeling.presentation.FeelingCategory
import com.zaidan.quraneasy.feature.feeling.presentation.VerseRef

@Preview(showBackground = true)
@Composable
private fun FeelingCardPreview() {
    FeelingCard(
        feeling = Feeling(
            id = "hope",
            emoji = "❤️",
            title = "Need Hope",
            subtitle = "Allah's mercy never ends",
            verses = listOf(VerseRef(39, 53)),
            category = FeelingCategory.Feeling
        ),
        onClick = {}
    )
}

@Composable
fun FeelingCard(
    feeling: Feeling,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = AppSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                feeling.emoji,
                style = MaterialTheme.typography.headlineMedium
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    feeling.title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    color = AppPrimaryText
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    feeling.subtitle,
                    color = AppSecondaryText,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Text(
                if (feeling.category == FeelingCategory.QuranDua) "Open" else "Explore",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
