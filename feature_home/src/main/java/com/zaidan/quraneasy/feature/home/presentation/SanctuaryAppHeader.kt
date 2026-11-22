package com.zaidan.quraneasy.feature.home.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaidan.quraneasy.core.ui.TourCoordinator
import com.zaidan.quraneasy.core.ui.tourTarget

@Composable
fun SanctuaryAppHeader(
    section: String,
    onAboutClick: () -> Unit,
    onBackClick: () -> Unit,
    tourCoordinator: TourCoordinator? = null
) {
    val colors = MaterialTheme.colorScheme
    Surface(color = colors.background) {
        Row(Modifier.fillMaxWidth().statusBarsPadding().height(56.dp).padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onBackClick,
                modifier = if (tourCoordinator != null && section == "Read Quran") 
                    Modifier.tourTarget(tourCoordinator, "back_button") 
                    else Modifier
            ) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Back")
            }
            Text(section, Modifier.weight(1f).padding(horizontal = 8.dp),
                fontSize = 20.sp, fontWeight = FontWeight.SemiBold, maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
            IconButton(onClick = onAboutClick) { Icon(Icons.Outlined.Info, "About Quran Easy") }
        }
    }
}
