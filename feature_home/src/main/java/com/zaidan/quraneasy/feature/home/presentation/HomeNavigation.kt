package com.zaidan.quraneasy.feature.home.presentation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.zaidan.quraneasy.core.navigation.Screen

fun NavGraphBuilder.homeGraph(
    navController: NavHostController
) {
    composable(Screen.Home.route) {
        HomeScreen(
            onReadQuranClick = { navController.navigate(Screen.Quran.route) },
            onTasbihClick = { navController.navigate(Screen.Tasbih.route) },
            onDhikrClick = { navController.navigate(Screen.Dhikr.route) }
        )
    }
}
