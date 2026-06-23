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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaidan.quraneasy.core.theme.AppBackground
import com.zaidan.quraneasy.core.theme.AppDimens
import com.zaidan.quraneasy.feature.feeling.presentation.Feeling
import com.zaidan.quraneasy.feature.feeling.presentation.FeelingViewModel
import com.zaidan.quraneasy.feature.feeling.presentation.carousel.FeelingCarousel
import com.zaidan.quraneasy.feature.prayer.presentation.PrayerTrackerCardFeature
import com.zaidan.quraneasy.feature.prayer.presentation.viewmodel.PrayerViewModel

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        onReadQuranClick = {},
        onTasbihClick = {},
        onAboutClick = {},
        onFeelingClick = {},
        feelings = emptyList(),
        prayerViewModel = viewModel()
    )
}

@Composable
fun HomeScreen(
    onReadQuranClick: () -> Unit,
    onTasbihClick: () -> Unit,
    onAboutClick: () -> Unit,
    onFeelingClick: (Feeling) -> Unit,
    prayerViewModel: PrayerViewModel,
    feelingViewModel: FeelingViewModel = hiltViewModel()
) {
    val feelings by feelingViewModel.homeFeelings.collectAsState()
    HomeScreen(
        onReadQuranClick = onReadQuranClick,
        onTasbihClick = onTasbihClick,
        onAboutClick = onAboutClick,
        onFeelingClick = onFeelingClick,
        feelings = feelings,
        prayerViewModel = prayerViewModel
    )
}

@Composable
fun HomeScreen(
    onReadQuranClick: () -> Unit,
    onTasbihClick: () -> Unit,
    onAboutClick: () -> Unit,
    onFeelingClick: (Feeling) -> Unit,
    feelings: List<Feeling>,
    prayerViewModel: PrayerViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground),
        verticalArrangement = Arrangement.Top
    ) {
        HomeHeader(onAboutClick = onAboutClick)

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
                onTasbihClick = onTasbihClick
            )
            Spacer(Modifier.height(20.dp))

            Text(
                "How are you feeling today?",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(12.dp))

            FeelingCarousel(
                feelings = feelings,
                onClick = onFeelingClick
            )

            Spacer(Modifier.height(24.dp))
            PrayerTrackerCardFeature(
                prayerViewModel = prayerViewModel
            )
            HomeFooter()
        }
    }
}
