package com.zaidan.quraneasy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.zaidan.quraneasy.feature.home.presentation.HomeScreen
import com.zaidan.quraneasy.feature.quran.presentation.QuranScreen
import com.zaidan.quraneasy.feature.tasbih.presentation.TasbihScreen
import com.zaidan.quraneasy.core.navigation.Screen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            MaterialTheme {
                Scaffold { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Home.route) {
                            HomeScreen(
                                onReadQuranClick = { navController.navigate(Screen.Quran.route) },
                                onTasbihClick = { navController.navigate(Screen.Tasbih.route) },
                                onDhikrClick = { navController.navigate(Screen.Dhikr.route) }
                            )
                        }
                        composable(Screen.Quran.route) {
                            QuranScreen(
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                        composable(Screen.Tasbih.route) {
                            TasbihScreen(
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }

    }
}
