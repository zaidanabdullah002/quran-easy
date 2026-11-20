package com.zaidan.quraneasy.feature.quran.presentation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.BookmarkRemove
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaidan.quraneasy.core.theme.LocalAppSettings
import com.zaidan.quraneasy.core.theme.QuranFont
import com.zaidan.quraneasy.core.theme.SanctuaryTypography
import com.zaidan.quraneasy.core.ui.AppSkeletonBlock
import com.zaidan.quraneasy.core.ui.HapticIconButton
import com.zaidan.quraneasy.core.ui.QuranEasyActionSurface
import com.zaidan.quraneasy.core.ui.QuranEasySurface
import com.zaidan.quraneasy.feature.quran.presentation.model.*
import com.zaidan.quraneasy.feature.quran.presentation.viewmodel.QuranViewModel
import java.text.DateFormat
import java.util.Date

@Composable
fun QuranScreen(
    onBackClick: () -> Unit,
    onSurahClick: (Int) -> Unit,
    onJuzClick: (Int) -> Unit,
    quranViewModel: QuranViewModel
) {
    val state by quranViewModel.uiState.collectAsStateWithLifecycle()
    MaterialTheme(typography = SanctuaryTypography) {
        QuranScreenContent(state, onBackClick, onSurahClick, onJuzClick,
            quranViewModel::selectTab, quranViewModel::retry, quranViewModel::removeBookmark)
    }
}

@Composable
internal fun QuranScreenContent(
    state: QuranUiState,
    onBackClick: () -> Unit,
    onSurahClick: (Int) -> Unit,
    onJuzClick: (Int) -> Unit,
    selectTab: (Int) -> Unit,
    retry: () -> Unit,
    removeBookmark: (QuranBookmarkUiModel) -> Unit
) {
    var query by rememberSaveable { mutableStateOf("") }
    var filter by rememberSaveable { mutableStateOf(ChapterFilter.ALL) }
    val colors = MaterialTheme.colorScheme
    val surahs = state.surahs.filter { matchesSurah(it, query, filter) }
    val juzs = state.juzs.filter {
        query.isBlank() || "${it.englishName} ${it.rangeDescription}".contains(query.trim(), true) ||
            it.juzNum.toString().startsWith(query.trim())
    }
    val bookmarks = state.bookmarks.filter { matchesBookmark(it, query) }
    val surahScroll = rememberLazyListState()
    val juzScroll = rememberLazyListState()
    val bookmarkScroll = rememberLazyListState()
    LaunchedEffect(query, filter) {
        surahScroll.scrollToItem(0)
        juzScroll.scrollToItem(0)
        bookmarkScroll.scrollToItem(0)
    }
    Column(Modifier.fillMaxSize().background(colors.background)) {
        TextField(value = query, onValueChange = { query = it }, singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)
                .border(1.dp, colors.outlineVariant, RoundedCornerShape(12.dp)),
            placeholder = { Text(when (state.selectedTab) {
                1 -> "Search Juz by number or chapter"
                2 -> "Search your bookmarks"
                else -> "Search Surah by name or number"
            }, fontSize = 14.sp) },
            leadingIcon = { Icon(Icons.Outlined.Search, null) },
            trailingIcon = { if (query.isNotEmpty()) HapticIconButton(onClick = { query = "" }) {
                Icon(Icons.Outlined.Close, "Clear search")
            } },
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = colors.surfaceContainerLowest,
                unfocusedContainerColor = colors.surfaceContainerLowest,
                focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent))
        Row(Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)).background(colors.surfaceContainerLowest)
            .border(1.dp, colors.outlineVariant, RoundedCornerShape(12.dp)).padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            BrowserTab("Surah", state.surahs.size, Icons.AutoMirrored.Outlined.MenuBook,
                state.selectedTab == 0, { selectTab(0) }, Modifier.weight(1f))
            BrowserTab("Juz", state.juzs.size, Icons.Outlined.GridView,
                state.selectedTab == 1, { selectTab(1) }, Modifier.weight(.85f))
            BrowserTab("Bookmarks", state.bookmarks.size, Icons.Outlined.BookmarkBorder,
                state.selectedTab == 2, { selectTab(2) }, Modifier.weight(1.45f))
        }
        if (state.message.isNotEmpty()) {
            QuranEasySurface(modifier = Modifier.padding(horizontal = 16.dp), border = null,
                containerColor = colors.errorContainer, contentColor = colors.onErrorContainer) {
                Text(state.message, style = MaterialTheme.typography.bodyMedium)
                TextButton(onClick = retry, enabled = !state.isLoading) { Text("Retry") }
            }
        }
        if (state.isLoading && state.surahs.isNotEmpty()) {
            LinearProgressIndicator(Modifier.fillMaxWidth())
        }
        if (!state.isLoading && state.message.isNotEmpty() && state.surahs.isEmpty()) return@Column
        if (state.isLoading && state.surahs.isEmpty()) {
            LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(6) { AppSkeletonBlock(Modifier.fillMaxWidth().height(96.dp).clip(RoundedCornerShape(16.dp))) }
            }
        } else when (state.selectedTab) {
            0 -> {
                Row(Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ChapterFilter.entries.forEach { choice ->
                        val count = state.surahs.count { choice.accepts(it) }
                        FilterChip(shape = RoundedCornerShape(50.dp), selected = filter == choice, onClick = { filter = choice },
                            enabled = choice == ChapterFilter.ALL || state.surahs.any { it.revelationType.isNotEmpty() },
                            label = { Text(if (choice == ChapterFilter.ALL) choice.label else "${choice.label} ($count)") },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = colors.surfaceContainer,
                                labelColor = colors.onSurface,
                                selectedContainerColor = if (colors.background.luminance() < .2f) colors.secondary else colors.primary,
                                selectedLabelColor = if (colors.background.luminance() < .2f) colors.onSecondary else colors.onPrimary,
                                disabledContainerColor = colors.surfaceContainer.copy(alpha = .45f),
                                disabledLabelColor = colors.onSurfaceVariant.copy(alpha = .55f),
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = filter == choice,
                                borderColor = colors.outlineVariant,
                                selectedBorderColor = Color.Transparent,
                                borderWidth = 1.dp,
                                selectedBorderWidth = 0.dp,
                            ))
                    }
                }
                if (state.surahs.isNotEmpty() && state.surahs.any { it.revelationType.isEmpty() }) {
                    Row(Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("Chapter details unavailable offline", Modifier.weight(1f),
                            fontSize = 12.sp, color = colors.onSurfaceVariant)
                        TextButton(onClick = retry, enabled = !state.isLoading) { Text("Retry") }
                    }
                }
                LazyColumn(state = surahScroll, contentPadding = PaddingValues(16.dp, 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                    if (surahs.isEmpty()) item {
                        BrowserEmpty("No chapters found", "Try another name or number, or choose All Chapters.") {
                            query = ""; filter = ChapterFilter.ALL
                        }
                    }
                    items(surahs, key = { it.number }) { surah ->
                        SurahCard(surah, { onSurahClick(surah.number) })
                    }
                }
            }
            1 -> LazyColumn(state = juzScroll, contentPadding = PaddingValues(16.dp, 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                item {
                    QuranEasySurface(border = null, containerColor = colors.surfaceContainer) {
                        Text("${state.juzs.size} Portions", fontWeight = FontWeight.Bold)
                        Text("Read one Juz each day to complete the Quran in 30 days.",
                            style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant)
                    }
                }
                if (juzs.isEmpty()) item { BrowserEmpty("No Juz found", "Try a different number or chapter name.") { query = "" } }
                items(juzs, key = { it.juzNum }) { juz -> JuzCard(juz, { onJuzClick(juz.juzNum) }) }
            }
            else -> LazyColumn(state = bookmarkScroll, contentPadding = PaddingValues(16.dp, 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                if (bookmarks.isEmpty()) item {
                    BrowserEmpty(if (state.bookmarks.isEmpty()) "No Bookmarks Yet" else "No matching bookmarks",
                        if (state.bookmarks.isEmpty()) "Use the bookmark control while reading a Surah or Juz to save it here."
                        else "Try another name or number.",
                        if (state.bookmarks.isEmpty()) "Start Reading Quran" else "Clear search") {
                        query = ""
                        if (state.bookmarks.isEmpty()) { filter = ChapterFilter.ALL; selectTab(0) }
                    }
                }
                items(bookmarks, key = { it.id }) { bookmark ->
                    BookmarkCard(bookmark, modifier = Modifier.animateItem(fadeOutSpec = tween(250), placementSpec = tween(250)), onClick = {
                        when (bookmark) {
                            is QuranBookmarkUiModel.SurahBookmark -> onSurahClick(bookmark.surahNumber)
                            is QuranBookmarkUiModel.JuzBookmark -> onJuzClick(bookmark.juzNumber)
                        }
                    }, onRemove = { removeBookmark(bookmark) })
                }
            }
        }
    }
}

@Composable
private fun BrowserTab(title: String, count: Int, icon: ImageVector, active: Boolean,
    onClick: () -> Unit, modifier: Modifier) {
    val colors = MaterialTheme.colorScheme
    val background by animateColorAsState(if (active) colors.secondary else Color.Transparent,
        tween(200), label = "Tab selection")
    val foreground = if (active) colors.onSecondary else colors.onSurfaceVariant
    QuranEasyActionSurface(onClick, modifier.heightIn(min = 48.dp).semantics { selected = active; role = Role.Tab },
        shape = RoundedCornerShape(8.dp), border = null, containerColor = background,
        contentColor = foreground, contentPadding = PaddingValues(horizontal = 2.dp, vertical = 0.dp)) {
        Row(Modifier.fillMaxWidth().heightIn(min = 48.dp), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center) {
            Icon(icon, null, Modifier.size(14.dp))
            Spacer(Modifier.width(3.dp))
            Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            Spacer(Modifier.width(3.dp))
            Text("$count", Modifier.clip(RoundedCornerShape(20.dp))
                .background(foreground.copy(alpha = .12f)).padding(horizontal = 4.dp),
                fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

private enum class ChapterEmphasis { DEFAULT, OPENING, HEART, NIGHT }

private fun chapterEmphasis(number: Int) = when (number) {
    1 -> ChapterEmphasis.OPENING
    36 -> ChapterEmphasis.HEART
    67 -> ChapterEmphasis.NIGHT
    else -> ChapterEmphasis.DEFAULT
}

@Composable
private fun NumberBadge(number: Int, emphasis: ChapterEmphasis = ChapterEmphasis.DEFAULT) {
    val colors = MaterialTheme.colorScheme
    val background = when (emphasis) {
        ChapterEmphasis.OPENING -> colors.secondaryContainer
        ChapterEmphasis.HEART -> colors.secondary
        ChapterEmphasis.NIGHT -> colors.tertiaryContainer.copy(alpha = .7f)
        ChapterEmphasis.DEFAULT -> colors.surfaceContainer
    }
    val foreground = when (emphasis) {
        ChapterEmphasis.OPENING -> colors.onSecondaryContainer
        ChapterEmphasis.HEART -> colors.onSecondary
        ChapterEmphasis.NIGHT -> colors.onTertiaryContainer
        ChapterEmphasis.DEFAULT -> colors.onSurface
    }
    Box(Modifier.size(48.dp).clip(RoundedCornerShape(12.dp))
        .background(background),
        contentAlignment = Alignment.Center) {
        Text("$number", fontSize = 18.sp, fontWeight = FontWeight.Bold,
            color = foreground)
    }
}

@Composable
fun SurahCard(surah: SurahUiModel, onClick: () -> Unit = {}) {
    val colors = MaterialTheme.colorScheme
    val emphasis = chapterEmphasis(surah.number)
    val featureLabel = when (emphasis) {
        ChapterEmphasis.HEART -> "Heart of Quran"
        ChapterEmphasis.NIGHT -> "Night Protection"
        else -> null
    }
    QuranEasyActionSurface(onClick, Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, colors.outlineVariant),
        containerColor = if (emphasis == ChapterEmphasis.HEART) colors.surfaceContainerLow else colors.surfaceContainerLowest,
        elevation = 0.dp,
        contentPadding = PaddingValues(16.dp)) {
        Row(Modifier.fillMaxWidth().heightIn(min = 64.dp), verticalAlignment = Alignment.CenterVertically) {
            NumberBadge(surah.number, emphasis)
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(displaySurahName(surah.number, surah.englishName), fontSize = 18.sp,
                        fontWeight = FontWeight.Bold, color = colors.onSurface, maxLines = 1,
                        overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f, fill = false))
                    if (featureLabel != null) {
                        Spacer(Modifier.width(6.dp))
                        Text(featureLabel,
                            modifier = Modifier.clip(RoundedCornerShape(50.dp))
                                .background(if (emphasis == ChapterEmphasis.HEART) colors.secondaryContainer else colors.tertiaryContainer)
                                .padding(horizontal = 7.dp, vertical = 3.dp),
                            color = if (emphasis == ChapterEmphasis.HEART) colors.onSecondaryContainer else colors.onTertiaryContainer,
                            fontSize = 9.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                    }
                }
                if (LocalAppSettings.current.showTranslatedSurahNames) Text(surah.translation,
                    fontSize = 12.sp, color = colors.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text("${surah.verses} Ayahs", Modifier.clip(RoundedCornerShape(6.dp))
                        .background(colors.surfaceContainer).padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    val revelation = revelationLabel(surah.revelationType)
                    if (revelation.isNotEmpty()) Text("• $revelation", fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold, color = colors.secondary)
                }
            }
            Text(displayArabicSurahName(surah.arabicName),
                Modifier.padding(start = 8.dp).widthIn(max = 112.dp),
                fontFamily = QuranFont, fontSize = 26.sp, color = colors.onSurface,
                textAlign = TextAlign.End, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, null, Modifier.size(20.dp),
                tint = if (emphasis == ChapterEmphasis.HEART) colors.secondary else colors.onSurfaceVariant)
        }
    }
}

@Composable
fun JuzCard(juz: JuzUiModel, onClick: () -> Unit = {}) {
    val colors = MaterialTheme.colorScheme
    QuranEasyActionSurface(onClick, Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, colors.outlineVariant), containerColor = colors.surfaceContainerLowest, elevation = 0.dp,
        contentPadding = PaddingValues(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            NumberBadge(juz.juzNum)
            Column(Modifier.weight(1f).padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(juz.englishName, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(juz.rangeDescription.ifEmpty { "Open to download this Juz" },
                    fontSize = 12.sp, color = colors.onSurfaceVariant)
                if (juz.verses > 0) Text("${juz.verses} Ayahs • ${juz.surahCount} ${if (juz.surahCount == 1) "Surah" else "Surahs"}",
                    fontSize = 10.sp, fontWeight = FontWeight.Bold, color = colors.onSurfaceVariant)
            }
            Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, null, Modifier.size(20.dp), tint = colors.outlineVariant)
        }
    }
}

@Composable
private fun BookmarkCard(bookmark: QuranBookmarkUiModel, modifier: Modifier = Modifier, onClick: () -> Unit, onRemove: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    QuranEasyActionSurface(onClick, modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, colors.outlineVariant),
        containerColor = if (colors.background.luminance() < .2f) colors.surfaceContainerHigh else colors.surfaceContainerLowest, elevation = 0.dp,
        contentPadding = PaddingValues(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            NumberBadge(when (bookmark) {
                is QuranBookmarkUiModel.SurahBookmark -> bookmark.surahNumber
                is QuranBookmarkUiModel.JuzBookmark -> bookmark.juzNumber
            })
            Column(Modifier.weight(1f).padding(start = 12.dp)) {
                Text(when (bookmark) {
                    is QuranBookmarkUiModel.SurahBookmark -> displaySurahName(bookmark.surahNumber, bookmark.englishName)
                    is QuranBookmarkUiModel.JuzBookmark -> "Juz ${bookmark.juzNumber}"
                }, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                // Legacy seeded bookmarks use timestamps of 1–3 milliseconds, not saved dates.
                if (bookmark.createdAt > 1_000L) Text("Saved ${DateFormat.getDateInstance(DateFormat.MEDIUM).format(Date(bookmark.createdAt))}",
                    fontSize = 10.sp, color = colors.onSurfaceVariant)
            }
            HapticIconButton(onClick = onRemove) { Icon(Icons.Outlined.BookmarkRemove, "Remove bookmark") }
        }
        if (bookmark is QuranBookmarkUiModel.SurahBookmark) {
            Column(Modifier.padding(top = 12.dp).fillMaxWidth().clip(RoundedCornerShape(12.dp))
                .background(colors.surfaceContainerLow).padding(12.dp)) {
                Text(displayArabicSurahName(bookmark.arabicName), Modifier.fillMaxWidth(), fontFamily = QuranFont,
                    fontSize = 26.sp, textAlign = TextAlign.End, color = colors.onSurface)
                if (LocalAppSettings.current.showTranslatedSurahNames) Text(bookmark.translation,
                    fontSize = 12.sp, color = colors.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun BrowserEmpty(title: String, description: String, action: String = "Clear filters", onAction: () -> Unit) {
    QuranEasySurface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), border = null,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest, contentPadding = PaddingValues(24.dp)) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Icon(Icons.Outlined.BookmarkBorder, null, Modifier.size(48.dp), tint = MaterialTheme.colorScheme.secondary)
            Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Text(description, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
            Button(onClick = onAction) { Text(action) }
        }
    }
}
