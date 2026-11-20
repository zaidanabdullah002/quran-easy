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
import androidx.compose.material.icons.outlined.AutoStories
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

@Composable
fun SanctuaryBottomNavigation(
    onReadQuranClick: () -> Unit,
    onTasbihClick: () -> Unit,
    onManzilClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onHomeClick: () -> Unit = {},
    selectedRoute: String = Screen.Home.route,
    modifier: Modifier = Modifier
) {
    if (selectedRoute == Screen.Home.route) {
        HomeTheme { NavigationContent(onReadQuranClick, onTasbihClick, onManzilClick, onSettingsClick, onHomeClick, selectedRoute, modifier) }
    } else {
        NavigationContent(onReadQuranClick, onTasbihClick, onManzilClick, onSettingsClick, onHomeClick, selectedRoute, modifier)
    }
}

@Composable
private fun NavigationContent(
    onReadQuranClick: () -> Unit, onTasbihClick: () -> Unit,
    onManzilClick: () -> Unit, onSettingsClick: () -> Unit, onHomeClick: () -> Unit,
    selectedRoute: String, modifier: Modifier,
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
                BottomNavItem(Icons.Outlined.AutoStories, "Read Quran", selectedRoute == Screen.Quran.route, onReadQuranClick, homePalette = selectedRoute == Screen.Home.route)
                BottomNavItem(Icons.Outlined.Gesture, "Tasbih", selectedRoute == Screen.Tasbih.route, onTasbihClick, homePalette = selectedRoute == Screen.Home.route)
                BottomNavItem(Icons.Outlined.CalendarViewWeek, "Manzil", selectedRoute == Screen.Manzil.route, onManzilClick, homePalette = selectedRoute == Screen.Home.route)
                BottomNavItem(Icons.Outlined.Settings, "Settings", selectedRoute == Screen.Settings.route, onSettingsClick, homePalette = selectedRoute == Screen.Home.route)
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
) {
    val colors = MaterialTheme.colorScheme
    val dark = colors.background.luminance() < .2f
    val color = if (selected) {
        if (dark && !homePalette) androidx.compose.ui.graphics.Color(0xFF38BDF8) else colors.secondary
    } else colors.onSurfaceVariant
    Column(
        modifier = Modifier
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
