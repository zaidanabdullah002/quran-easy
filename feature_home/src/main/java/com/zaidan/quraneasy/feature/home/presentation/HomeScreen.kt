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
                .padding(horizontal = 14.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            HomeHeader()
            Spacer(modifier = Modifier.height(34.dp))
            FeatureCardsSection(
                modifier = Modifier.fillMaxWidth(),
                onReadQuranClick = onReadQuranClick,
                onTasbihClick = onTasbihClick,
                onDhikrClick = onDhikrClick
            )
            Spacer(modifier = Modifier.height(20.dp))
            PrayerTrackerCard()
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
