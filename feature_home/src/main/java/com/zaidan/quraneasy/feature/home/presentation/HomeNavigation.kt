package com.zaidan.quraneasy.feature.home.presentation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.zaidan.quraneasy.core.navigation.Screen
import com.zaidan.quraneasy.core.ui.TourCoordinator
import com.zaidan.quraneasy.feature.feeling.presentation.FEELING_ROUTE_BASE
import com.zaidan.quraneasy.feature.prayer.presentation.viewmodel.PrayerViewModel

fun NavGraphBuilder.homeGraph(
    navController: NavHostController,
    tourCoordinator: TourCoordinator
) {
    composable(Screen.Home.route) {
        val prayerViewModel : PrayerViewModel = hiltViewModel()
        HomeScreen(
            onReadQuranClick = {
                if (tourCoordinator.currentStep?.targetId == "read_quran_card") {
                    tourCoordinator.nextStep()
                }
                navController.navigate(Screen.Quran.route)
            }, onAboutClick = { navController.navigate(Screen.About.route) },
            onSettingsClick = { navController.navigate(Screen.Settings.route) { launchSingleTop = true } },
            onFeelingClick = { feeling ->
                navController.navigate("$FEELING_ROUTE_BASE/${feeling.id}")
            },
            prayerViewModel = prayerViewModel,
            tourCoordinator = tourCoordinator
        )
    }

    composable(Screen.About.route) {
        AboutScreen(
            onBackClick = { navController.popBackStack() }
        )
    }
}
