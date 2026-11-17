package com.zaidan.quraneasy.feature.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaidan.quraneasy.feature.feeling.presentation.Feeling
import com.zaidan.quraneasy.feature.feeling.presentation.HomeFeelingViewModel
import com.zaidan.quraneasy.feature.feeling.presentation.carousel.FeelingCarousel
import com.zaidan.quraneasy.feature.prayer.presentation.PrayerTrackerCardFeature
import com.zaidan.quraneasy.feature.prayer.presentation.viewmodel.PrayerViewModel

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        onReadQuranClick = {},
        onTasbihClick = {},
        onManzilClick = {},
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
    onManzilClick: () -> Unit,
    onAboutClick: () -> Unit,
    onFeelingClick: (Feeling) -> Unit,
    prayerViewModel: PrayerViewModel,
    feelingViewModel: HomeFeelingViewModel = hiltViewModel(),
    onSettingsClick: () -> Unit = {}
) {
    val feelings by feelingViewModel.feelings.collectAsStateWithLifecycle()
    HomeScreen(
        onReadQuranClick = onReadQuranClick,
        onTasbihClick = onTasbihClick,
        onManzilClick = onManzilClick,
        onAboutClick = onAboutClick,
        onSettingsClick = onSettingsClick,
        onFeelingClick = onFeelingClick,
        feelings = feelings,
        prayerViewModel = prayerViewModel
    )
}

@Composable
fun HomeScreen(
    onReadQuranClick: () -> Unit,
    onTasbihClick: () -> Unit,
    onManzilClick: () -> Unit,
    onAboutClick: () -> Unit,
    onFeelingClick: (Feeling) -> Unit,
    feelings: List<Feeling>,
    prayerViewModel: PrayerViewModel,
    onSettingsClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Top
    ) {
        HomeHeader(onAboutClick = onAboutClick, onSettingsClick = onSettingsClick)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            FeatureCardsSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                onReadQuranClick = onReadQuranClick,
                onTasbihClick = onTasbihClick,
                onManzilClick = onManzilClick
            )
            Spacer(Modifier.height(16.dp))

            Text(
                "How are you feeling today?",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(Modifier.height(12.dp))

            FeelingCarousel(
                feelings = feelings,
                onClick = onFeelingClick
            )

            Spacer(Modifier.height(24.dp))
            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                PrayerTrackerCardFeature(
                    prayerViewModel = prayerViewModel
                )
            }
            HomeFooter()
        }
    }
}
