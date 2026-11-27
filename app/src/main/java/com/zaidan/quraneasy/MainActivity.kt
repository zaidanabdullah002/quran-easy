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
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.composable
import com.zaidan.quraneasy.qibla.QiblaScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.zaidan.quraneasy.feature.home.presentation.SanctuaryBottomNavigation
import com.zaidan.quraneasy.feature.home.presentation.SanctuaryAppHeader
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.zaidan.quraneasy.core.navigation.Screen
import com.zaidan.quraneasy.core.settings.AppSettings
import com.zaidan.quraneasy.core.settings.SettingsRepository
import com.zaidan.quraneasy.core.settings.ThemeMode
import com.zaidan.quraneasy.core.settings.TourStatus
import com.zaidan.quraneasy.core.theme.QuranEasyTheme
import com.zaidan.quraneasy.core.ui.ThemeSelectionDialog
import com.zaidan.quraneasy.core.ui.TourCoordinator
import com.zaidan.quraneasy.core.ui.TourOverlay
import com.zaidan.quraneasy.feature.feeling.data.FeelingRepository
import com.zaidan.quraneasy.feature.feeling.presentation.feelingGraph
import com.zaidan.quraneasy.feature.home.presentation.homeGraph
import com.zaidan.quraneasy.feature.manzil.domain.manzilPassages
import com.zaidan.quraneasy.feature.manzil.presentation.manzilGraph
import com.zaidan.quraneasy.feature.quran.data.translation.AyahTranslationRepository
import com.zaidan.quraneasy.feature.quran.domain.repository.QuranRepository
import com.zaidan.quraneasy.feature.quran.presentation.quranGraph
import com.zaidan.quraneasy.feature.tasbih.presentation.tasbihGraph
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retryWhen
import java.io.IOException
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaidan.quraneasy.feature.settings.presentation.settingsGraph
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var feelingRepository: FeelingRepository

    @Inject
    lateinit var settingsRepository: SettingsRepository

    @Inject
    lateinit var quranRepository: QuranRepository

    @Inject
    lateinit var translationRepository: AyahTranslationRepository

    @Inject
    lateinit var tourCoordinator: TourCoordinator

    private var settingsReady = false
    private var dataPreloaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition { !settingsReady || !dataPreloaded }
        super.onCreate(savedInstanceState)

        preloadHomeData()

        enableEdgeToEdge()
        setContent {
            val settingsFlow = remember(settingsRepository) {
                var lastKnownSettings = AppSettings()
                settingsRepository.settings
                    .map<AppSettings, AppSettings?> { lastKnownSettings = it; it }
                    .retryWhen { cause, _ ->
                        if (cause is IOException) {
                            emit(lastKnownSettings)
                            delay(2000)
                            true
                        } else false
                    }
            }
            val appSettings by settingsFlow.collectAsStateWithLifecycle(initialValue = null)
            val resolvedSettings = appSettings ?: return@setContent
            settingsReady = true

            LaunchedEffect(resolvedSettings.tourStatus) {
                tourCoordinator.initialize(resolvedSettings.tourStatus)
            }

            val navController = rememberNavController()
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = backStackEntry?.destination?.route ?: Screen.Home.route
            val mainRoutes = setOf(Screen.Qibla.route, Screen.Home.route, Screen.Quran.route, Screen.Tasbih.route, Screen.Manzil.route, Screen.Settings.route)
            val navigateTab: (Screen) -> Unit = { screen ->
                if (navController.currentDestination?.route != screen.route) {
                    if (screen == Screen.Home) {
                        // Home is the root, not a saved tab stack to restore.
                        navController.popBackStack(Screen.Home.route, inclusive = false, saveState = true)
                    } else {
                        navController.navigate(screen.route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            }
            var showLaunchAnimation by rememberSaveable {
                mutableStateOf(savedInstanceState == null)
            }

            val tourActive = tourCoordinator.currentStep != null

            QuranEasyTheme(settings = resolvedSettings, route = currentRoute) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        contentWindowInsets = WindowInsets(0),
                        topBar = {
                            if (currentRoute in mainRoutes && currentRoute != Screen.Home.route) {
                                SanctuaryAppHeader(
                                    section = when (currentRoute) {
                                        Screen.Qibla.route -> "Qibla"
                                        Screen.Quran.route -> "Read Quran"
                                        Screen.Tasbih.route -> "Tasbih Counter"
                                        Screen.Manzil.route -> "Daily Manzil"
                                        else -> "Settings"
                                    },
                                    onBackClick = {
                                        if (tourCoordinator.currentStep?.targetId == "back_button") {
                                            tourCoordinator.nextStep()
                                        }
                                        onBackPressedDispatcher.onBackPressed()
                                    },
                                    onAboutClick = { navController.navigate(Screen.About.route) { launchSingleTop = true } },
                                    tourCoordinator = tourCoordinator
                                )
                            }
                        },
                        bottomBar = {
                            if (currentRoute in mainRoutes) SanctuaryBottomNavigation(
                                onHomeClick = { navigateTab(Screen.Home) },
                                onQiblaClick = { navigateTab(Screen.Qibla) },
                                onTasbihClick = { navigateTab(Screen.Tasbih) },
                                onManzilClick = { navigateTab(Screen.Manzil) },
                                onSettingsClick = {
                                    if (tourCoordinator.currentStep?.targetId == "settings_nav") {
                                        tourCoordinator.nextStep()
                                        lifecycleScope.launch {
                                            settingsRepository.setTourStatus(TourStatus.TOUR_COMPLETED)
                                        }
                                    }
                                    navigateTab(Screen.Settings)
                                },
                                selectedRoute = currentRoute,
                                tourCoordinator = tourCoordinator
                            )
                        },
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = Screen.Home.route,
                            modifier = Modifier.padding(innerPadding).consumeWindowInsets(innerPadding)
                        ) {
                            composable(Screen.Qibla.route) { QiblaScreen() }
                            homeGraph(navController, tourCoordinator)
                            quranGraph(navController, tourCoordinator)
                            tasbihGraph(navController)
                            manzilGraph(navController)
                            feelingGraph(navController)
                            settingsGraph(navController, tourCoordinator)
                        }
                    }

                    if (resolvedSettings.tourStatus == TourStatus.NOT_STARTED || resolvedSettings.tourStatus == TourStatus.THEME_PENDING) {
                        ThemeSelectionDialog(
                            onThemeSelected = { mode ->
                                lifecycleScope.launch {
                                    settingsRepository.setThemeMode(mode)
                                    settingsRepository.setTourStatus(TourStatus.TOUR_ACTIVE)
                                    tourCoordinator.startTour()
                                }
                            }
                        )
                    } else if (tourActive) {
                        TourOverlay(
                            coordinator = tourCoordinator,
                            onExitTour = {
                                tourCoordinator.exitTour()
                                lifecycleScope.launch {
                                    settingsRepository.setTourStatus(TourStatus.TOUR_EXITED)
                                }
                            }
                        )
                    }

                    if (showLaunchAnimation) {
                        QuranEasyLaunchAnimation(
                            onFinished = { showLaunchAnimation = false }
                        )
                    }
                }
            }
        }
    }

    private fun preloadHomeData() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                coroutineScope {
                    // 1. Download all feeling first verse
                    val feelings = feelingRepository.getHomeFeelings()
                    feelings.forEach { feeling ->
                        launch {
                            feeling.verses.firstOrNull()?.let { verse ->
                                quranRepository.ensureSurahDownloaded(verse.surah)
                                translationRepository.getTranslation(verse.surah, verse.ayah)
                            }
                        }
                    }

                    // 2. Download all manzil
                    quranRepository.ensureSurahListDownloaded()
                    manzilPassages
                        .groupBy { it.surahNumber }
                        .forEach { (surahNumber, passages) ->
                            launch {
                                quranRepository.ensureSurahDownloaded(surahNumber)
                                val requestedAyahs = passages.flatMap { it.startAyah..it.endAyah }.toSet()
                                translationRepository.getSurahTranslations(surahNumber, requestedAyahs)
                            }
                        }
                }
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                // Allow the app to open even if preloading fails
            } finally {
                dataPreloaded = true
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
            .background(colorResource(R.color.quran_easy_launch_background)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.mipmap.quran_app_logo),
            contentDescription = null,
            modifier = Modifier
                .graphicsLayer {
                    translationY = verticalOffset.value
                    this.alpha = alpha.value
                }
                .size(156.dp)
                .clip(CircleShape)
        )
    }
}

private const val ENTER_DURATION_MILLIS = 800
private const val CENTER_HOLD_MILLIS = 700
private const val EXIT_DURATION_MILLIS = 700
