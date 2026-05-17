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
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zaidan.quraneasy.core.theme.AppDimens

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview(){
    HomeScreen(
        onReadQuranClick = {},
        onTasbihClick = {},
        onDhikrClick = {}
    )
}

@Composable
fun HomeScreen(
    onReadQuranClick: () -> Unit,
    onTasbihClick: () -> Unit,
    onDhikrClick: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(PageBackground)
                .padding(innerPadding)
                .padding(horizontal = AppDimens.ScreenPaddingSmall.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(AppDimens.ItemSpacing.dp))
            HomeHeader()
            Spacer(modifier = Modifier.height(AppDimens.ExtraLargeSpacing.dp))
            FeatureCardsSection(
                modifier = Modifier.fillMaxWidth(),
                onReadQuranClick = onReadQuranClick,
                onTasbihClick = onTasbihClick,
                onDhikrClick = onDhikrClick
            )
            Spacer(modifier = Modifier.height(AppDimens.LargeSectionSpacing.dp))
            PrayerTrackerCard()
            Spacer(modifier = Modifier.height(AppDimens.ScreenPaddingLarge.dp))
        }
    }
}
