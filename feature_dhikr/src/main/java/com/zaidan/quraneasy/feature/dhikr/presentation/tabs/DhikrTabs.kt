package com.zaidan.quraneasy.feature.dhikr.presentation.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaidan.quraneasy.core.theme.AppDimens
import com.zaidan.quraneasy.feature.dhikr.presentation.DhikrCategory

private val NeutralText = Color(0xFF1D1D1F)
private val NeutralSubtext = Color(0xFF747C8C)
private val SelectedChip = Color(0xFFFDFDFD)
private val UnselectedChip = Color(0xFFF0F0F2)

@Preview(showBackground = true)
@Composable
private fun DhikrTabsPreview() {
    DhikrCategoryStrip(
        categories = listOf(
            DhikrCategory("Morning", "After Fajr", selected = true),
            DhikrCategory("Evening", "After Maghrib"),
            DhikrCategory("After Prayer", "Post salah"),
            DhikrCategory("General", "Any time")
        ),
        onCategoryClick = {},
        modifier = Modifier.padding(14.dp)
    )
}

@Composable
fun DhikrCategoryStrip(
    categories: List<DhikrCategory>,
    onCategoryClick: (DhikrCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(AppDimens.ItemSpacing.dp)
    ) {
        categories.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppDimens.ItemSpacing.dp)
            ) {
                rowItems.forEach { category ->
                    DhikrCategoryChip(
                        category = category,
                        onClick = { onCategoryClick(category) },
                        modifier = Modifier
                            .weight(1f)
                            .height(AppDimens.TabHeight.dp)
                    )
                }
                if (rowItems.size == 1) {
                    Box(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DhikrCategoryChipPreview() {
    DhikrCategoryChip(
        category = DhikrCategory("Morning", "After Fajr", selected = true),
        onClick = {},
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun DhikrCategoryChip(
    category: DhikrCategory,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(AppDimens.SmallRadius.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (category.selected) SelectedChip else UnselectedChip
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = AppDimens.ScreenPaddingSmall.dp,
                    vertical = AppDimens.ScreenPaddingSmall.dp
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = category.title,
                    color = if (category.selected) NeutralText else NeutralSubtext,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                if (category.subtitle.isNotBlank()) {
                    Text(
                        text = category.subtitle,
                        color = NeutralSubtext,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
