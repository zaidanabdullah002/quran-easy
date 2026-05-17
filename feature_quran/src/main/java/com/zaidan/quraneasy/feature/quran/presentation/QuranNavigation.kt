package com.zaidan.quraneasy.feature.quran.presentation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.zaidan.quraneasy.core.navigation.Screen
import androidx.navigation.NavType
import androidx.navigation.navArgument

fun NavGraphBuilder.quranGraph(
    navController: NavHostController
) {
    composable(Screen.Quran.route) {
        QuranScreen(
            onBackClick = { navController.popBackStack() },
            onSurahClick = { surahNumber -> navController.navigate(QuranRoutes.reader(surahNumber)) }
        )
    }

    composable(
        route = QuranRoutes.reader,
        arguments = listOf(navArgument("surahNumber") { type = NavType.IntType })
    ) { backStackEntry ->
        QuranReaderScreen(
            onBackClick = { navController.popBackStack() },
            surahNumber = backStackEntry.arguments?.getInt("surahNumber") ?: 1
        )
    }
}
