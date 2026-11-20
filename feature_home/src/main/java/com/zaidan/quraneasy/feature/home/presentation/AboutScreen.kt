package com.zaidan.quraneasy.feature.home.presentation

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaidan.quraneasy.core.theme.QuranFont
import com.zaidan.quraneasy.core.ui.SanctuaryBanner

@Composable
fun AboutScreen(onBackClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val context = LocalContext.current
    val version = remember(context) {
        runCatching { context.packageManager.getPackageInfo(context.packageName, 0).versionName }.getOrNull().orEmpty()
    }
    val storeUrl = "https://play.google.com/store/apps/details?id=${context.packageName}"
    Column(Modifier.fillMaxSize().background(colors.background).statusBarsPadding()) {
        Row(Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
            Text("About", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).navigationBarsPadding().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Surface(color = colors.primary, shape = CircleShape, modifier = Modifier.size(88.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.AutoMirrored.Outlined.MenuBook, null, Modifier.size(40.dp), tint = colors.onPrimary)
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Quran Easy", fontSize = 26.sp, fontWeight = FontWeight.Bold)
                Text("القرآن الكريم", fontFamily = QuranFont, fontSize = 28.sp, color = colors.secondary)
                Surface(color = colors.surfaceVariant, shape = CircleShape) {
                    Text("Version $version", Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium)
                }
            }
            Text("Made for easy daily Quran reading and a calmer spiritual routine.",
                textAlign = TextAlign.Center, color = colors.onSurfaceVariant, lineHeight = 26.sp)
            Text("Made with devotion by Zaidan ❤️", style = MaterialTheme.typography.labelLarge)
            SanctuaryBanner(com.zaidan.quraneasy.core.R.drawable.sanctuary_about, "Sanctuary for your daily recitation")
            Text("CORE PILLARS", Modifier.fillMaxWidth(), color = colors.secondary,
                style = MaterialTheme.typography.labelLarge)
            AboutPillar(Icons.AutoMirrored.Outlined.MenuBook, "Daily Quran & Manzil",
                "Read Quran and selected passages in a focused reading space.")
            AboutPillar(Icons.Outlined.Spa, "Mindful Remembrance",
                "Keep a steady rhythm of dhikr with your personal Tasbih counter.")
            AboutPillar(Icons.Outlined.FavoriteBorder, "Spiritual Reflections",
                "Find Quranic verses for moments of hope, peace, and gratitude.")
            Surface(color = colors.surfaceContainerLow, shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Quran typography", fontWeight = FontWeight.SemiBold)
                    Text("Al Qalam script with adjustable reading size.", color = colors.onSurfaceVariant)
                    HorizontalDivider(color = colors.outlineVariant)
                    Text("Prayer times", fontWeight = FontWeight.SemiBold)
                    Text("Calculated for your selected location. Check the location and calculation settings when times differ.",
                        color = colors.onSurfaceVariant)
                }
            }
            Button(
                onClick = {
                    context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, "Quran Easy — a space for Quran, prayer, and reflection. $storeUrl")
                    }, "Share Quran Easy"))
                }, modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = colors.secondary),
            ) { Text("Share Quran Easy") }
            OutlinedButton(onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(storeUrl))) },
                modifier = Modifier.fillMaxWidth()) { Text("Rate on Google Play") }
        }
    }
}

@Composable
private fun AboutPillar(icon: ImageVector, title: String, detail: String) {
    val colors = MaterialTheme.colorScheme
    Surface(color = colors.surfaceContainerLowest, shape = RoundedCornerShape(16.dp)) {
        Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Icon(icon, null, tint = colors.secondary, modifier = Modifier.size(24.dp))
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(title, fontWeight = FontWeight.SemiBold)
                Text(detail, style = MaterialTheme.typography.bodyMedium, color = colors.onSurfaceVariant)
            }
        }
    }
}
