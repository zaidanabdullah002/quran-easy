package com.zaidan.quraneasy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.zaidan.quraneasy.core.navigation.Screen
import com.zaidan.quraneasy.core.theme.AppBackground
import com.zaidan.quraneasy.feature.feeling.data.FeelingRepository
import com.zaidan.quraneasy.feature.feeling.presentation.feelingGraph
import com.zaidan.quraneasy.feature.home.presentation.homeGraph
import com.zaidan.quraneasy.feature.manzil.presentation.manzilGraph
import com.zaidan.quraneasy.feature.quran.presentation.quranGraph
import com.zaidan.quraneasy.feature.tasbih.presentation.tasbihGraph
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var feelingRepository: FeelingRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        preloadHomeData()

        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            var showLaunchAnimation by rememberSaveable {
                mutableStateOf(savedInstanceState == null)
            }

            Box(modifier = Modifier.fillMaxSize()) {
                MaterialTheme {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        contentWindowInsets = WindowInsets(0)
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = Screen.Home.route,
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            homeGraph(navController)
                            quranGraph(navController)
                            tasbihGraph(navController)
                            manzilGraph(navController)
                            feelingGraph(navController)
                        }
                    }
                }

                if (showLaunchAnimation) {
                    QuranEasyLaunchAnimation(
                        onFinished = { showLaunchAnimation = false }
                    )
                }
            }
        }
    }

    private fun preloadHomeData() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                feelingRepository.getHomeFeelings()
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                // Home remains usable and owns its visible fallback state.
            }
        }
    }
}

@Composable
private fun QuranEasyLaunchAnimation(
    onFinished: () -> Unit
) {
    val travelDistance = with(LocalDensity.current) { 44.dp.toPx() }
    val verticalOffset = remember { Animatable(travelDistance) }
    val alpha = remember { Animatable(0f) }
    val currentOnFinished by rememberUpdatedState(onFinished)

    LaunchedEffect(Unit) {
        coroutineScope {
            launch {
                verticalOffset.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(
                        durationMillis = ENTER_DURATION_MILLIS,
                        easing = FastOutSlowInEasing
                    )
                )
            }
            launch {
                alpha.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = ENTER_DURATION_MILLIS)
                )
            }
        }

        delay(CENTER_HOLD_MILLIS.toLong())

        coroutineScope {
            launch {
                verticalOffset.animateTo(
                    targetValue = -travelDistance,
                    animationSpec = tween(
                        durationMillis = EXIT_DURATION_MILLIS,
                        easing = FastOutSlowInEasing
                    )
                )
            }
            launch {
                alpha.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = EXIT_DURATION_MILLIS)
                )
            }
        }

        currentOnFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.mipmap.quran_app_logo),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .graphicsLayer {
                    translationY = verticalOffset.value
                    this.alpha = alpha.value
                }
                .size(156.dp)
                .shadow(10.dp, CircleShape)
                .clip(CircleShape)
        )
    }
}

private const val ENTER_DURATION_MILLIS = 550
private const val CENTER_HOLD_MILLIS = 400
private const val EXIT_DURATION_MILLIS = 550
