package com.zaidan.quraneasy.feature.quran.presentation


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaidan.quraneasy.feature.quran.data.model.SurahUiModel

@Preview(showBackground = true)
@Composable
fun QuranScreenPreview(){
    QuranScreen(
        onBackClick = {}
    )
}

@Composable
fun QuranScreen(
    onBackClick: () -> Unit
) {

    val surahs = listOf(
        SurahUiModel(1, "Al-Fatihah", "The Opening", 7, "الفاتحة"),
        SurahUiModel(2, "Al-Baqarah", "The Cow", 286, "البقرة"),
        SurahUiModel(112, "Al-Ikhlas", "The Sincerity", 4, "الإخلاص"),
        SurahUiModel(113, "Al-Falaq", "The Daybreak", 5, "الفلق"),
        SurahUiModel(114, "An-Nas", "Mankind", 6, "الناس"),
        SurahUiModel(114, "An-Nas", "Mankind", 6, "الناس"),
        SurahUiModel(114, "An-Nas", "Mankind", 6, "الناس"),
        SurahUiModel(1, "Al-Fatihah", "The Opening", 7, "الفاتحة"),
        SurahUiModel(2, "Al-Baqarah", "The Cow", 286, "البقرة"),
        SurahUiModel(112, "Al-Ikhlas", "The Sincerity", 4, "الإخلاص"),
        SurahUiModel(113, "Al-Falaq", "The Daybreak", 5, "الفلق"),
        SurahUiModel(114, "An-Nas", "Mankind", 6, "الناس"),
        SurahUiModel(114, "An-Nas", "Mankind", 6, "الناس"),
        SurahUiModel(114, "An-Nas", "Mankind", 6, "الناس"),
    )

    var selectedTab by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F3F3))
    ) {

        TopSection(
            selectedTab = selectedTab,
            onTabSelected = {selectedTab = it},
            onBackClick = onBackClick)

        LazyColumn(
            contentPadding = PaddingValues(
                horizontal = 12.dp,
                vertical = 18.dp
            ),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            items(surahs) { surah ->
                SurahCard(surah)
            }
        }
    }
}

@Composable
fun TopSection(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    onBackClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF252525))
            .padding(top = 14.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp)
        ) {

            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Outlined.KeyboardArrowLeft,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(34.dp)
                )
            }

            Text(
                text = "Read Quran",
                color = Color.White,
                fontSize = 25.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Row(
            modifier = Modifier
                .padding(horizontal = 14.dp)
                .fillMaxWidth()
                .height(82.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF3B3B3B))
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            QuranTab(
                selected = selectedTab == 0,
                icon = {
                    Icon(
                        Icons.Outlined.MenuBook,
                        contentDescription = null
                    )
                },
                title = "Surah",
                onClick = {
                    onTabSelected(0)
                },
                modifier = Modifier.weight(1f)
            )

            QuranTab(
                selected = selectedTab == 1,
                icon = {
                    Icon(
                        Icons.Outlined.Layers,
                        contentDescription = null
                    )
                },
                title = "Juz",
                onClick = {
                    onTabSelected(1)
                },
                modifier = Modifier.weight(1f)
            )

            QuranTab(
                selected = selectedTab == 2,
                icon = {
                    Icon(
                        Icons.Outlined.BookmarkBorder,
                        contentDescription = null
                    )
                },
                title = "Bookmarks",
                onClick = {
                    onTabSelected(2)
                },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))
    }
}

@Composable
fun QuranTab(
    selected: Boolean,
    icon: @Composable () -> Unit,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxHeight(),
        shape = RoundedCornerShape(20.dp),
        color = if (selected)
            Color.White
        else
            Color.Transparent
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            CompositionLocalProvider(
                LocalContentColor provides
                        if (selected) Color(0xFF202020)
                        else Color.White.copy(alpha = 0.85f)
            ) {

                icon()

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun SurahCard(
    surah: SurahUiModel
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 18.dp,
                    vertical = 22.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF232323)),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = surah.number.toString(),
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(18.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = surah.englishName,
                    fontSize = 23.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF151515)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${surah.translation} • ${surah.verses} verses",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF73798B)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = surah.arabicName,
                fontSize = 36.sp,
                color = Color.Black,
                textAlign = TextAlign.End,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}