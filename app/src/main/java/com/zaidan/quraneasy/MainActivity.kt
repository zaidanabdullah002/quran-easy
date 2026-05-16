package com.zaidan.quraneasy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.zaidan.quraneasy.feature.home.presentation.HomeScreen
import com.zaidan.quraneasy.feature.quran.presentation.QuranScreen
import com.zaidan.quraneasy.core.navigation.Screen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            MaterialTheme {
                HomeScreen(navController)
            }
            NavHost(navController = navController,
                startDestination = Screen.Home.route
            ){
                composable(Screen.Home.route) {
                    HomeScreen(navController)
                }
                composable(Screen.Quran.route) {
                    QuranScreen()
                }
            }
        }

    }
}
