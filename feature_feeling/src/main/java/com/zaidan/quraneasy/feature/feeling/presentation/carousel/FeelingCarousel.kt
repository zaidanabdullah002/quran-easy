package com.zaidan.quraneasy.feature.feeling.presentation.carousel

import androidx.compose.foundation.background
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zaidan.quraneasy.feature.feeling.presentation.Feeling
import com.zaidan.quraneasy.feature.feeling.presentation.FeelingCategory
import com.zaidan.quraneasy.feature.feeling.presentation.VerseRef
import com.zaidan.quraneasy.feature.feeling.presentation.cards.FeelingCard

@Preview(showBackground = true)
@Composable
fun FeelingCarouselPreview() {
    FeelingCarousel(
        feelings = listOf(
            Feeling(
                id = "hope",
                emoji = "❤️",
                title = "Need Hope",
                subtitle = "Allah's mercy never ends",
                verses = listOf(VerseRef(39, 53)),
                category = FeelingCategory.Feeling
            ),
            Feeling(
                id = "dua",
                emoji = "🤲",
                title = "Quran Dua",
                subtitle = "Short duas from the Quran",
                verses = listOf(VerseRef(2, 286)),
                category = FeelingCategory.QuranDua
            )
        ),
        onClick = {}
    )
}

@Composable
fun FeelingCarousel(
    feelings: List<Feeling>,
    onClick: (Feeling) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { feelings.size })

    Column {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 20.dp),
            pageSpacing = 12.dp
        ) { page ->
            FeelingCard(
                feeling = feelings[page],
                onClick = { onClick(feelings[page]) }
            )
        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(feelings.size) { index ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (pagerState.currentPage == index) 8.dp else 6.dp)
                        .clip(CircleShape)
                        .background(
                            if (pagerState.currentPage == index) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                Color.Gray.copy(alpha = 0.35f)
                            }
                        )
                )
            }
        }
    }
}

