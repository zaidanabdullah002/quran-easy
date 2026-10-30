package com.zaidan.quraneasy.core.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object About : Screen("about")
    object Quran : Screen("quran")
    object Tasbih : Screen("tasbih")
    object Manzil : Screen("manzil")
    object ManzilPassage : Screen("manzil/passage/{index}") {
        fun route(index: Int) = "manzil/passage/$index"
    }
    object Prayer : Screen("prayer")
    object Feeling : Screen("feeling")
}
