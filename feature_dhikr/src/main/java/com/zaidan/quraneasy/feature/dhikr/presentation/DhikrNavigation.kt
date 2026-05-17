package com.zaidan.quraneasy.feature.dhikr.presentation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.zaidan.quraneasy.core.navigation.Screen

fun NavGraphBuilder.dhikrGraph(
    navController: NavHostController
) {
    composable(Screen.Dhikr.route) {
        DhikrScreen(
            onBackClick = { navController.popBackStack() }
        )
    }
}
