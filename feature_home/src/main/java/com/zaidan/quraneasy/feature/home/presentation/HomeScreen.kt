package com.zaidan.quraneasy.feature.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zaidan.quraneasy.core.theme.AppBackground
import com.zaidan.quraneasy.core.theme.AppDimens
import com.zaidan.quraneasy.feature.prayer.domain.Prayer
import com.zaidan.quraneasy.feature.prayer.presentation.PrayerTrackerCard
import com.zaidan.quraneasy.feature.prayer.presentation.PrayerUiState
import com.zaidan.quraneasy.feature.prayer.presentation.viewmodel.PrayerViewModel

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        onReadQuranClick = {},
        onTasbihClick = {},
        onDhikrClick = {},
        prayerUiState = PrayerUiState(
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
fun HomeScreen(
    onReadQuranClick: () -> Unit,
    onTasbihClick: () -> Unit,
    onDhikrClick: () -> Unit,
    prayerViewModel: PrayerViewModel
) {
    val prayerUiState by prayerViewModel.uiStateFlow.collectAsState()
    HomeScreen(
        onReadQuranClick = onReadQuranClick,
        onTasbihClick = onTasbihClick,
        onDhikrClick = onDhikrClick,
        prayerUiState = prayerUiState,
        onTogglePrayer = { index -> prayerViewModel.togglePrayer(index) }
    )
}

@Composable
fun HomeScreen(
    onReadQuranClick: () -> Unit,
    onTasbihClick: () -> Unit,
    onDhikrClick: () -> Unit,
    prayerUiState: PrayerUiState,
    onTogglePrayer: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground),
        verticalArrangement = Arrangement.Top
    ) {
        HomeHeader()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = AppDimens.ScreenPaddingSmall.dp)
        ) {
            Spacer(modifier = Modifier.height(AppDimens.LargeSectionSpacing.dp))
            FeatureCardsSection(
                modifier = Modifier.fillMaxWidth(),
                onReadQuranClick = onReadQuranClick,
                onTasbihClick = onTasbihClick,
                onDhikrClick = onDhikrClick
            )
            Spacer(modifier = Modifier.height(AppDimens.LargeSectionSpacing.dp))
            PrayerTrackerCard(
                uiState = prayerUiState,
                onTogglePrayer = onTogglePrayer
            )
            HomeFooter()

        }
    }
}
