package com.zaidan.quraneasy.feature.quran.presentation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.zaidan.quraneasy.core.navigation.Screen
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.zaidan.quraneasy.feature.quran.presentation.viewmodel.QuranViewModel
import com.zaidan.quraneasy.feature.quran.presentation.viewmodel.QuranReaderViewModel

fun NavGraphBuilder.quranGraph(
    navController: NavHostController
) {
    composable(Screen.Quran.route) {
        val quranViewModel: QuranViewModel = hiltViewModel()

        QuranScreen(
            quranViewModel = quranViewModel,
            onBackClick = { navController.popBackStack() },
            onSurahClick = { surahNumber -> navController.navigate(QuranRoutes.reader(ReaderType.SURAH.ordinal,surahNumber)) },
            onJuzClick = { juzNumber -> navController.navigate(QuranRoutes.reader(ReaderType.JUZ.ordinal,juzNumber)) }
        )
    }

    composable(
        route = QuranRoutes.reader ,
        arguments = listOf(
            navArgument("readerType") { type = NavType.IntType },
            navArgument("itemNumber") { type = NavType.IntType }
            )
    ) { backStackEntry ->
        val quranReaderViewModel: QuranReaderViewModel = hiltViewModel()
        QuranReaderScreen(
            quranReaderViewModel = quranReaderViewModel,
            onBackClick = { navController.popBackStack() },
            readerType = backStackEntry.arguments?.getInt("readerType") ?: 0,
            itemNumber = backStackEntry.arguments?.getInt("itemNumber") ?: 1
        )
    }
}
