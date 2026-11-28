package com.zaidan.quraneasy.feature.home.presentation

import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.MaterialTheme
import com.zaidan.quraneasy.core.navigation.Screen
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.CalendarViewWeek
import androidx.compose.material.icons.outlined.Gesture
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaidan.quraneasy.core.ui.TourCoordinator
import com.zaidan.quraneasy.core.ui.tourTarget

@Composable
fun SanctuaryBottomNavigation(
    onQiblaClick: () -> Unit,
    onTasbihClick: () -> Unit,
    onManzilClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onHomeClick: () -> Unit = {},
    selectedRoute: String = Screen.Home.route,
    modifier: Modifier = Modifier,
    tourCoordinator: TourCoordinator? = null
) {
    if (selectedRoute == Screen.Home.route) {
        HomeTheme {
            NavigationContent(
                onQiblaClick = onQiblaClick,
                onTasbihClick = onTasbihClick,
                onManzilClick = onManzilClick,
                onSettingsClick = onSettingsClick,
                onHomeClick = onHomeClick,
                selectedRoute = selectedRoute,
                modifier = modifier,
                tourCoordinator = tourCoordinator
            )
        }
    } else {
        NavigationContent(
            onQiblaClick = onQiblaClick,
            onTasbihClick = onTasbihClick,
            onManzilClick = onManzilClick,
            onSettingsClick = onSettingsClick,
            onHomeClick = onHomeClick,
            selectedRoute = selectedRoute,
            modifier = modifier,
            tourCoordinator = tourCoordinator
        )
    }
}

@Composable
private fun NavigationContent(
    onQiblaClick: () -> Unit,
    onTasbihClick: () -> Unit,
    onManzilClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onHomeClick: () -> Unit,
    selectedRoute: String,
    modifier: Modifier,
    tourCoordinator: TourCoordinator? = null
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background,
        shadowElevation = 6.dp
    ) {
        Column(modifier = Modifier.navigationBarsPadding()) {
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectableGroup()
                    .padding(top = 8.dp, bottom = 7.dp)
            ) {
                BottomNavItem(Icons.Default.Home, "Home", selectedRoute == Screen.Home.route, onHomeClick, homePalette = selectedRoute == Screen.Home.route)
                
                BottomNavItem(
                    icon = Icons.Outlined.Gesture,
                    label = "Tasbih",
                    selected = selectedRoute == Screen.Tasbih.route,
                    onClick = onTasbihClick,
                    homePalette = selectedRoute == Screen.Home.route,
                    modifier = if (tourCoordinator != null) Modifier.tourTarget(tourCoordinator, "tasbih_nav") else Modifier
                )
                
                BottomNavItem(
                    icon = Icons.Outlined.Explore,
                    label = "Qibla",
                    selected = selectedRoute == Screen.Qibla.route,
                    onClick = onQiblaClick,
                    homePalette = selectedRoute == Screen.Home.route,
                    modifier = if (tourCoordinator != null) Modifier.tourTarget(tourCoordinator, "qibla_nav") else Modifier
                )

                BottomNavItem(Icons.Outlined.CalendarViewWeek, "Manzil", selectedRoute == Screen.Manzil.route, onManzilClick, homePalette = selectedRoute == Screen.Home.route)

                BottomNavItem(
                    icon = Icons.Outlined.Settings,
                    label = "Settings",
                    selected = selectedRoute == Screen.Settings.route,
                    onClick = onSettingsClick,
                    homePalette = selectedRoute == Screen.Home.route,
                    modifier = if (tourCoordinator != null) Modifier.tourTarget(tourCoordinator, "settings_nav") else Modifier
                )
            }
        }
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.BottomNavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    homePalette: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val dark = colors.background.luminance() < .2f
    val color = if (selected) {
        if (dark && !homePalette) androidx.compose.ui.graphics.Color(0xFF38BDF8) else colors.secondary
    } else colors.onSurfaceVariant
    Column(
        modifier = modifier
            .weight(1f)
            .heightIn(min = 48.dp)
            .selectable(selected = selected, role = Role.Tab, onClick = onClick)
            .padding(vertical = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = label,
            color = color,
            fontSize = 10.sp,
            lineHeight = 16.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
