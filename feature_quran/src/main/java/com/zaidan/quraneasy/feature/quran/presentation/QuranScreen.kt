package com.zaidan.quraneasy.feature.quran.presentation


import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaidan.quraneasy.core.ui.AppCardDefaults
import com.zaidan.quraneasy.core.ui.HapticIconButton
import com.zaidan.quraneasy.core.ui.hapticClick
import com.zaidan.quraneasy.core.ui.AppErrorView
import com.zaidan.quraneasy.core.ui.AppLoadingView
import com.zaidan.quraneasy.feature.quran.presentation.model.JuzUiModel
import com.zaidan.quraneasy.feature.quran.presentation.model.QuranUiState
import com.zaidan.quraneasy.feature.quran.presentation.model.SurahUiModel
import com.zaidan.quraneasy.feature.quran.presentation.model.dummyData.juzList
import com.zaidan.quraneasy.feature.quran.presentation.model.dummyData.surahList
import com.zaidan.quraneasy.feature.quran.presentation.viewmodel.QuranViewModel
import my.nanihadesuka.compose.LazyColumnScrollbar

private val toggleHeight = 60.dp
private val iconSize = 20.dp
private val titleFontSize = 18.sp

private val topSectionPadding = 2.dp

@Preview(showBackground = true)
@Composable
fun QuranScreenPreview() {
    QuranScreenContent(
        QuranUiState(
            selectedTab = 0,
            isLoading = false,
            isReady = true,
            surahs = surahList,
            juzs = juzList
        ),
        onBackClick = {},
        onSurahClick = {},
        onJuzClick = {},
        onBookmarkClick = {},
        selectTab = {}
    )
}

@Composable
fun QuranScreen(
    onBackClick: () -> Unit,
    onSurahClick: (Int) -> Unit,
    onJuzClick: (Int) -> Unit,
    onBookmarkClick: () -> Unit,
    quranViewModel: QuranViewModel
) {
    val uiState = quranViewModel.uiState.collectAsStateWithLifecycle().value
    QuranScreenContent(
        uiState = uiState,
        selectTab = quranViewModel::selectTab,
        onBackClick = onBackClick,
        onSurahClick = onSurahClick,
        onJuzClick = onJuzClick,
        onBookmarkClick = onBookmarkClick
    )
}

@Composable
private fun QuranScreenContent(
    uiState: QuranUiState,
    onBackClick: () -> Unit,
    onSurahClick: (Int) -> Unit,
    onJuzClick: (Int) -> Unit,
    onBookmarkClick: () -> Unit,
    selectTab: (Int) -> Unit
) {
    val TAG = "QuranScreenContent"
    Log.i(TAG, "uiState : $uiState")
    val surahs = uiState.surahs
    val juz = uiState.juzs
    val selectedTab = uiState.selectedTab
    val surahListState = rememberLazyListState()
    val juzListState = rememberLazyListState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F3F3))
    ) {
        TopSection(
            selectedTab = selectedTab,
            onTabSelected = { selectTab(it) },
            onBackClick = onBackClick
        )

        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                AppLoadingView(
                    title = "Downloading surah list",
                    subtitle = "Preparing the Quran index for offline browsing"
                )
            }
        } else if (uiState.message.isNotEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                AppErrorView(message = uiState.message)
            }
        } else {
            when (selectedTab) {
                ReaderType.SURAH.ordinal -> LazyColumnScrollbar(
                    state = surahListState,
                    settings = quranScrollbarSettings()
                ) {
                    LazyColumn(
                        state = surahListState,
                        contentPadding = PaddingValues(
                            horizontal = 12.dp,
                            vertical = 18.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(surahs) { surah ->
                            SurahCard(
                                surah = surah,
                                onClick = { onSurahClick(surah.number) }
                            )
                        }
                    }
                }

                ReaderType.JUZ.ordinal -> LazyColumnScrollbar(
                    state = juzListState,
                    settings = quranScrollbarSettings()
                ) {
                    LazyColumn(
                        state = juzListState,
                        contentPadding = PaddingValues(
                            horizontal = 12.dp,
                            vertical = 18.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(juz) { juz ->
                            JuzCard(
                                juz = juz,
                                onClick = { onJuzClick(juz.juzNum) }
                            )
                        }
                    }
                }

                else -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    BookmarkEmptyState()
                }
            }
        }
    }
}

@Composable
private fun BookmarkEmptyState() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(horizontal = 28.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(104.dp)
                .clip(RoundedCornerShape(34.dp))
                .background(Color(0xFF1F1F1F))
        ) {
            Icon(
                imageVector = Icons.Outlined.FavoriteBorder,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(42.dp)
            )
        }

        Text(
            text = "No bookmarks yet",
            color = Color(0xFF1A1A1A),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Save verses here to revisit your favorite ayahs anytime.",
            color = Color(0xFF6F7681),
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
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
            .statusBarsPadding()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            HapticIconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Outlined.KeyboardArrowLeft,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(iconSize)
                )
            }

            Text(
                text = "Read Quran",
                color = Color.White,
                fontSize = titleFontSize,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(topSectionPadding))

        Row(
            modifier = Modifier
                .padding(horizontal = 14.dp)
                .fillMaxWidth()
                .height(toggleHeight)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF363636))
                .padding(6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QuranTab(
                selected = selectedTab == 0,
                icon = {
                    Icon(
                        Icons.Filled.Menu,
                        contentDescription = null
                    )
                },
                title = "Surah",
                onClick = {
                    onTabSelected(ReaderType.SURAH.ordinal)
                },
                modifier = Modifier.weight(1f)
            )

            QuranTab(
                selected = selectedTab == 1,
                icon = {
                    Icon(
                        Icons.Outlined.DateRange,
                        contentDescription = null
                    )
                },
                title = "Juz",
                onClick = {
                    onTabSelected(ReaderType.JUZ.ordinal)
                },
                modifier = Modifier.weight(1f)
            )

            QuranTab(
                selected = selectedTab == 2,
                icon = {
                    Icon(
                        Icons.Outlined.FavoriteBorder,
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
        onClick = hapticClick(onClick = onClick),
        modifier = modifier.fillMaxHeight(),
        shape = RoundedCornerShape(18.dp),
        color = if (selected)
            Color.White
        else
            Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = topSectionPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            CompositionLocalProvider(
                LocalContentColor provides
                        if (selected) Color(0xFF202020)
                        else Color.White.copy(alpha = 0.85f)
            ) {
                icon()

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun SurahCard(
    surah: SurahUiModel,
    onClick: () -> Unit = {}
) {
    Card(
        onClick = hapticClick(onClick = onClick),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(1.dp, Color(0xFFE8E2D8)),
        elevation = AppCardDefaults.interactiveElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 124.dp)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(74.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF1F1F1F)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = surah.number.toString(),
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(82.dp)
                    .background(Color(0xFFE9E2D7))
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = surah.englishName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A1A1A),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = surah.arabicName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF444444),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = surah.translation,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF6F7681),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "${surah.verses} verses",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF8B6F47),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun JuzCard(
    juz: JuzUiModel,
    onClick: () -> Unit = {}
) {
    Card(
        onClick = hapticClick(onClick = onClick),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(1.dp, Color(0xFFE8E2D8)),
        elevation = AppCardDefaults.interactiveElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 104.dp)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(74.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF1F1F1F)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = juz.juzNum.toString(),
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(58.dp)
                    .background(Color(0xFFE9E2D7))
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = juz.englishName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A1A1A),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "Juz ${juz.juzNum}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF6F7681),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "${juz.verses} verses",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF8B6F47),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
