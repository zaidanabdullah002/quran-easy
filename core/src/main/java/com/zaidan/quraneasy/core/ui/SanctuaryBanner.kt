package com.zaidan.quraneasy.core.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.luminance
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SanctuaryBanner(@DrawableRes image: Int, title: String, description: String = "") {
    val colors = MaterialTheme.colorScheme
    val dark = colors.background.luminance() < .2f
    Box(Modifier.fillMaxWidth().height(144.dp).clip(RoundedCornerShape(20.dp))
        .border(1.dp, colors.outlineVariant, RoundedCornerShape(20.dp))) {
        Image(painterResource(image), contentDescription = null, contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize())
        Box(Modifier.fillMaxSize().background(Brush.horizontalGradient(if (dark) listOf(Color(0xF5000000), Color(0xD90E1512), Color.Transparent)
            else listOf(Color(0xE61E2E24), Color(0x221E2E24)))))
        Column(Modifier.align(Alignment.BottomStart).padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, color = if (dark) colors.secondary else Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            if (description.isNotEmpty()) Text(description, color = Color.White, fontSize = 14.sp, lineHeight = 22.sp)
        }
    }
}
