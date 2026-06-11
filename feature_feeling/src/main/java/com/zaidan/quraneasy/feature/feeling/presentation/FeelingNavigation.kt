package com.zaidan.quraneasy.feature.feeling.presentation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.zaidan.quraneasy.core.navigation.Screen

const val FEELING_ROUTE_BASE = "feeling"
const val FEELING_ROUTE_PATTERN = "feeling/{feelingId}"

fun NavGraphBuilder.feelingGraph(
    navController: NavHostController
) {
    composable(
        route = FEELING_ROUTE_PATTERN,
        arguments = listOf(navArgument("feelingId") { type = NavType.StringType })
    ) { backStackEntry ->
        val viewModel: FeelingViewModel = hiltViewModel(backStackEntry)
        FeelingDetailScreen(
            viewModel = viewModel,
            onBackClick = { navController.popBackStack(Screen.Home.route, false) }
        )
    }
}

