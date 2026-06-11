package com.zaidan.quraneasy.feature.home.presentation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.zaidan.quraneasy.core.navigation.Screen
import com.zaidan.quraneasy.feature.feeling.presentation.FEELING_ROUTE_BASE
import com.zaidan.quraneasy.feature.prayer.presentation.viewmodel.PrayerViewModel

fun NavGraphBuilder.homeGraph(
    navController: NavHostController
) {
    composable(Screen.Home.route) {
        val prayerViewModel : PrayerViewModel = hiltViewModel()
        HomeScreen(
            onReadQuranClick = { navController.navigate(Screen.Quran.route) },
            onTasbihClick = { navController.navigate(Screen.Tasbih.route) },
            onFeelingClick = { feeling ->
                navController.navigate("$FEELING_ROUTE_BASE/${feeling.id}")
            },
            prayerViewModel = prayerViewModel
        )
    }
}
