package com.zaidan.quraneasy.feature.tasbih.presentation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.zaidan.quraneasy.core.navigation.Screen

fun NavGraphBuilder.tasbihGraph(
    navController: NavHostController
) {
    composable(Screen.Tasbih.route) {
        TasbihScreen(
            onBackClick = { navController.popBackStack() }
        )
    }
}
