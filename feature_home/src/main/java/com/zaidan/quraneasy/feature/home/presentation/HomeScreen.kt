package com.zaidan.quraneasy.feature.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaidan.quraneasy.core.ui.TourCoordinator
import com.zaidan.quraneasy.feature.feeling.presentation.Feeling
import com.zaidan.quraneasy.feature.feeling.presentation.FeelingVerseUiModel
import com.zaidan.quraneasy.feature.feeling.presentation.HomeFeelingViewModel
import com.zaidan.quraneasy.feature.prayer.presentation.PrayerTrackerCardFeature
import com.zaidan.quraneasy.feature.prayer.presentation.viewmodel.PrayerViewModel

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        onReadQuranClick = {},
        onAboutClick = {},
        onFeelingClick = {},
        feelings = emptyList(),
        prayerViewModel = viewModel()
    )
}

@Composable
fun HomeScreen(
    onReadQuranClick: () -> Unit,
    onAboutClick: () -> Unit,
    onFeelingClick: (Feeling) -> Unit,
    prayerViewModel: PrayerViewModel,
    feelingViewModel: HomeFeelingViewModel = hiltViewModel(),
    onSettingsClick: () -> Unit = {},
    tourCoordinator: TourCoordinator? = null
) {
    val feelings by feelingViewModel.feelings.collectAsStateWithLifecycle()
    val spotlights by feelingViewModel.spotlights.collectAsStateWithLifecycle()
    HomeScreen(
        onReadQuranClick = onReadQuranClick,
        onAboutClick = onAboutClick,
        onSettingsClick = onSettingsClick,
        onFeelingClick = onFeelingClick,
        feelings = feelings,
        spotlights = spotlights,
        onLoadSpotlight = feelingViewModel::loadSpotlight,
        prayerViewModel = prayerViewModel,
        tourCoordinator = tourCoordinator
    )
}

@Composable
fun HomeScreen(
    onReadQuranClick: () -> Unit,
    onAboutClick: () -> Unit,
    onFeelingClick: (Feeling) -> Unit,
    feelings: List<Feeling>,
    prayerViewModel: PrayerViewModel,
    spotlights: Map<String, FeelingVerseUiModel> = emptyMap(),
    onLoadSpotlight: (String) -> Unit = {},
    onSettingsClick: () -> Unit = {},
    tourCoordinator: TourCoordinator? = null
) = HomeTheme {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SanctuaryColors.Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 16.dp)
        ) {
            HomeHeader(
                onAboutClick = onAboutClick,
                onSettingsClick = onSettingsClick
            )

            FeatureCardsSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                onReadQuranClick = onReadQuranClick,
                tourCoordinator = tourCoordinator
            )

            Spacer(modifier = Modifier.height(20.dp))

            SanctuarySection(
                feelings = feelings,
                spotlights = spotlights,
                onLoadSpotlight = onLoadSpotlight,
                onFeelingClick = onFeelingClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                PrayerTrackerCardFeature(
                    prayerViewModel = prayerViewModel,
                    compact = true
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

    }
}
