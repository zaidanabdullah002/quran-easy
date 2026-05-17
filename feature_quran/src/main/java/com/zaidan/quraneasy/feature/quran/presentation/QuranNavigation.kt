package com.zaidan.quraneasy.feature.quran.presentation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.zaidan.quraneasy.core.navigation.Screen

fun NavGraphBuilder.quranGraph(
    navController: NavHostController
) {
    composable(Screen.Quran.route) {
        QuranScreen(
            onBackClick = { navController.popBackStack() }
        )
    }
}
