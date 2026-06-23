package com.zaidan.quraneasy.core.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object About : Screen("about")
    object Quran : Screen("quran")
    object Tasbih : Screen("tasbih")
    object Prayer : Screen("prayer")
    object Feeling : Screen("feeling")
}
