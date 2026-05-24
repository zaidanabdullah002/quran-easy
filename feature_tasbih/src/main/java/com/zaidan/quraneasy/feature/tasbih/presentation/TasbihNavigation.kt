package com.zaidan.quraneasy.feature.tasbih.presentation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.zaidan.quraneasy.core.navigation.Screen
import com.zaidan.quraneasy.feature.tasbih.presentation.viewmodel.TasbihViewModel

fun NavGraphBuilder.tasbihGraph(
    navController: NavHostController
) {
    composable(Screen.Tasbih.route) {
        val viewModel: TasbihViewModel = hiltViewModel()
        TasbihScreen(
            onBackClick = { navController.popBackStack() },
            viewModel = viewModel
        )
    }
}
