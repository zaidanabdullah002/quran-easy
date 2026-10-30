package com.zaidan.quraneasy.feature.manzil.presentation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.zaidan.quraneasy.core.navigation.Screen

fun NavGraphBuilder.manzilGraph(
    navController: NavHostController,
) {
    composable(Screen.Manzil.route) {
        ManzilListScreen(
            onBackClick = { navController.popBackStack() },
            onPassageClick = { index ->
                navController.navigate(Screen.ManzilPassage.route(index))
            },
        )
    }

    composable(
        route = Screen.ManzilPassage.route,
        arguments = listOf(navArgument("index") { type = NavType.IntType }),
    ) {
        val viewModel: ManzilViewModel = hiltViewModel()
        ManzilDetailScreen(
            onBackClick = { navController.popBackStack() },
            viewModel = viewModel,
        )
    }
}
