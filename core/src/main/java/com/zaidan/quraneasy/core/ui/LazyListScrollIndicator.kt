package com.zaidan.quraneasy.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.roundToInt

@Composable
fun LazyListScrollIndicator(
    listState: LazyListState,
    modifier: Modifier = Modifier,
    trackColor: Color = Color(0x14000000),
    thumbColor: Color = Color(0x66000000),
    collapsedWidth: Dp = 4.dp,
    expandedWidth: Dp = 10.dp,
    touchTargetWidth: Dp = 24.dp
) {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    var isDragging by remember { mutableStateOf(false) }
    val visualWidth by animateDpAsState(
        targetValue = if (isDragging) expandedWidth else collapsedWidth,
        animationSpec = tween(durationMillis = 140),
        label = "scrollbar_width"
    )

    val metrics by remember(listState) {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visibleItemsInfo = layoutInfo.visibleItemsInfo
            val totalItemsCount = layoutInfo.totalItemsCount

            if (totalItemsCount == 0 || visibleItemsInfo.isEmpty()) return@derivedStateOf null

            val viewportHeight = (layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset)
                .coerceAtLeast(0)
            if (viewportHeight == 0) return@derivedStateOf null

            val averageItemSize = visibleItemsInfo.map { it.size }.average().toFloat().coerceAtLeast(1f)
            val estimatedContentHeight = averageItemSize * totalItemsCount
            if (estimatedContentHeight <= viewportHeight) return@derivedStateOf null

            val firstVisibleItem = visibleItemsInfo.first()
            val scrollOffsetPx = ((firstVisibleItem.index * averageItemSize) - firstVisibleItem.offset)
                .coerceAtLeast(0f)
            val maxScrollPx = (estimatedContentHeight - viewportHeight).coerceAtLeast(1f)

            ScrollIndicatorMetrics(
                viewportHeightPx = viewportHeight.toFloat(),
                estimatedContentHeightPx = estimatedContentHeight,
                averageItemSizePx = averageItemSize,
                scrollOffsetPx = scrollOffsetPx,
                maxScrollPx = maxScrollPx
            )
        }
    }

    val indicator = metrics ?: return

    BoxWithConstraints(
        modifier = modifier
            .fillMaxHeight()
            .width(touchTargetWidth)
    ) {
        val trackHeightPx = with(density) { maxHeight.toPx() }.coerceAtLeast(1f)
        val thumbHeightPx = max(
            (indicator.viewportHeightPx * trackHeightPx / indicator.estimatedContentHeightPx).roundToInt(),
            with(density) { 36.dp.roundToPx() }
        ).coerceAtMost(trackHeightPx.roundToInt())
        val maxThumbOffsetPx = (trackHeightPx - thumbHeightPx).coerceAtLeast(0f)
        val thumbOffsetPx = if (maxThumbOffsetPx == 0f) {
            0f
        } else {
            (indicator.scrollOffsetPx / indicator.maxScrollPx * maxThumbOffsetPx).coerceIn(0f, maxThumbOffsetPx)
        }

        fun scrollFromThumbPosition(positionPx: Float) {
            val clampedOffsetPx = positionPx.coerceIn(0f, maxThumbOffsetPx)
            val progress = if (maxThumbOffsetPx == 0f) 0f else clampedOffsetPx / maxThumbOffsetPx
            val targetScrollPx = progress * indicator.maxScrollPx
            val targetIndex = (targetScrollPx / indicator.averageItemSizePx)
                .toInt()
                .coerceIn(0, (listState.layoutInfo.totalItemsCount - 1).coerceAtLeast(0))
            val itemOffset = (targetScrollPx - (targetIndex * indicator.averageItemSizePx))
                .roundToInt()
                .coerceAtLeast(0)

            scope.launch {
                listState.scrollToItem(targetIndex, itemOffset)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(touchTargetWidth)
                .pointerInput(
                    listState.layoutInfo.totalItemsCount,
                    thumbHeightPx,
                    maxThumbOffsetPx,
                    indicator.maxScrollPx
                ) {
                    detectDragGestures(
                        onDragStart = { offset: Offset ->
                            isDragging = true
                            scrollFromThumbPosition(offset.y - (thumbHeightPx / 2f))
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            scrollFromThumbPosition(
                                thumbOffsetPx + dragAmount.y
                            )
                        },
                        onDragEnd = {
                            isDragging = false
                        },
                        onDragCancel = {
                            isDragging = false
                        }
                    )
                }
        ) {
            Box(
                modifier = Modifier
                    .align(androidx.compose.ui.Alignment.CenterEnd)
                    .fillMaxHeight()
                    .width(visualWidth)
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(999.dp))
                    .background(trackColor)
            )
            Box(
                modifier = Modifier
                    .align(androidx.compose.ui.Alignment.TopEnd)
                    .offset { IntOffset(0, thumbOffsetPx.roundToInt()) }
                    .width(visualWidth)
                    .height(with(density) { thumbHeightPx.toDp() })
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(999.dp))
                    .background(thumbColor)
            )
        }
    }
}

private data class ScrollIndicatorMetrics(
    val viewportHeightPx: Float,
    val estimatedContentHeightPx: Float,
    val averageItemSizePx: Float,
    val scrollOffsetPx: Float,
    val maxScrollPx: Float
)
