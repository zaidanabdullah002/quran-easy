package com.zaidan.quraneasy.feature.feeling.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zaidan.quraneasy.core.theme.AppBackground
import com.zaidan.quraneasy.core.theme.AppPrimaryText
import com.zaidan.quraneasy.core.theme.AppSecondaryText
import com.zaidan.quraneasy.core.theme.AppSoftSurface
import com.zaidan.quraneasy.core.theme.AppSurface
import com.zaidan.quraneasy.feature.feeling.presentation.topbar.FeelingTopBar

@Composable
fun FeelingDetailScreen(
    viewModel: FeelingViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    FeelingDetailScreen(
        uiState = uiState,
        onBackClick = onBackClick
    )
}

@Composable
fun FeelingDetailScreen(
    uiState: FeelingDetailUiState,
    onBackClick: () -> Unit
) {
    val feeling = uiState.feeling
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .statusBarsPadding()
    ) {
        FeelingTopBar(
            title = feeling?.title ?: "Feeling",
            onBackClick = onBackClick
        )

        when {
            uiState.isLoading -> {
                Text(
                    text = "Loading...",
                    modifier = Modifier.padding(24.dp),
                    color = AppSecondaryText
                )
            }

            uiState.message != null -> {
                Text(
                    text = uiState.message,
                    modifier = Modifier.padding(24.dp),
                    color = AppSecondaryText
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = AppSurface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(Modifier.padding(20.dp)) {
                                Text(
                                    text = feeling?.emoji.orEmpty(),
                                    style = MaterialTheme.typography.displaySmall
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = feeling?.title.orEmpty(),
                                    style = MaterialTheme.typography.titleLarge,
                                    color = AppPrimaryText,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = feeling?.subtitle.orEmpty(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = AppSecondaryText
                                )
                            }
                        }
                    }

                    items(uiState.verses) { verse ->
                        VerseCard(
                            verse = verse
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VerseCard(
    verse: FeelingVerseUiModel
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = AppSoftSurface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(18.dp)) {
            Text(
                text = verse.arabicText,
                style = MaterialTheme.typography.headlineSmall,
                color = AppPrimaryText
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = "Surah ${verse.surahNumber}, Ayah ${verse.numberInSurah}",
                color = AppSecondaryText,
                style = MaterialTheme.typography.labelMedium
            )
            verse.translation?.takeIf { it.isNotBlank() }?.let {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = it,
                    color = AppPrimaryText,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FeelingDetailScreenPreview() {
    FeelingDetailScreen(
        uiState = FeelingDetailUiState(
            isLoading = false,
            feeling = Feeling(
                id = "hope",
                emoji = "❤️",
                title = "Need Hope",
                subtitle = "Allah's mercy never ends",
                verses = emptyList()
            ),
            verses = listOf(
                FeelingVerseUiModel(
                    surahNumber = 39,
                    numberInSurah = 53,
                    arabicText = "قُلْ يَٰعِبَادِيَ ٱلَّذِينَ أَسْرَفُوا۟",
                    translation = "Say, O My servants who have transgressed..."
                )
            )
        ),
        onBackClick = {}
    )
}
