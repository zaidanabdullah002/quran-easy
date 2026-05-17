package com.zaidan.quraneasy.feature.dhikr.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zaidan.quraneasy.core.theme.AppDimens
import com.zaidan.quraneasy.feature.dhikr.presentation.cards.DhikrCard
import com.zaidan.quraneasy.feature.dhikr.presentation.progress.DhikrProgressBar
import com.zaidan.quraneasy.feature.dhikr.presentation.tabs.DhikrCategoryStrip
import com.zaidan.quraneasy.feature.dhikr.presentation.topbar.DhikrTopBar

private val NeutralBackground = androidx.compose.ui.graphics.Color(0xFFF6F6F5)

@Preview(showBackground = true)
@Composable
fun DhikrScreenPreview() {
    DhikrScreen(onBackClick = {})
}

@Preview(showBackground = true)
@Composable
private fun DhikrScreenPreviewScrolled() {
    DhikrScreen(onBackClick = {})
}

@Composable
fun DhikrScreen(
    onBackClick: () -> Unit
) {
    var selectedCategoryIndex by rememberSaveable { mutableIntStateOf(0) }

    val categories = listOf(
        DhikrCategory("Morning", "After Fajr", selected = true),
        DhikrCategory("Evening", "After Maghrib"),
        DhikrCategory("After Prayer", "Post salah"),
        DhikrCategory("General", "Any time")
    )

    val items = listOf(
        DhikrItem(
            countLabel = "100x",
            reference = "Sahih Muslim 2691",
            arabic = "سُبْحَانَ اللهِ وَبِحَمْدِهِ",
            transliteration = "Subhanallahi wa bihamdihi",
            translation = "Glory be to Allah and praise Him"
        ),
        DhikrItem(
            countLabel = "100x",
            reference = "Tirmidhi 3467",
            arabic = "سُبْحَانَ اللهِ الْعَظِيمِ وَبِحَمْدِهِ",
            transliteration = "Subhanallahil-Adheem wa bihamdihi",
            translation = "Glory be to Allah, the Supreme, and praise Him"
        ),
        DhikrItem(
            countLabel = "33x",
            reference = "Sahih Bukhari 3414",
            arabic = "الْحَمْدُ لِلَّهِ",
            transliteration = "Alhamdulillah",
            translation = "All praise is due to Allah"
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NeutralBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = (AppDimens.FooterHeight + AppDimens.ScreenPadding).dp)
                .padding(horizontal = AppDimens.ScreenPaddingSmall.dp),
            verticalArrangement = Arrangement.spacedBy(AppDimens.SectionSpacing.dp)
        ) {
            DhikrTopBar(
                title = "Daily Dhikr",
                onBackClick = onBackClick
            )

            DhikrCategoryStrip(
                categories = categories.mapIndexed { index, category ->
                    category.copy(selected = index == selectedCategoryIndex)
                },
                onCategoryClick = { category ->
                    selectedCategoryIndex = categories.indexOfFirst { it.title == category.title }
                        .coerceAtLeast(0)
                },
                modifier = Modifier.padding(vertical = AppDimens.ScreenPadding.dp)
            )

            items.forEach { item ->
                DhikrCard(item = item)
            }

            Spacer(modifier = Modifier.height(AppDimens.ScreenPadding.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 0.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            DhikrProgressBar(
                completed = 0,
                total = 4
            )
        }
    }
}
