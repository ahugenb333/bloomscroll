package com.ahugenb.bloomscroll.lazylist

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import scroll.ScrollDirection
import scroll.ScrollUnit

class LazyListWrapper(
    private val lazyListState: LazyListState,
    private val scope: CoroutineScope,
    private val density: Float,
    private val scrollDirection: ScrollDirection = ScrollDirection.FORWARD,
    private val isDummyItem: (position: Int) -> Boolean,
    private val onItemScrolled: (ScrollUnit) -> Unit,
    private val onCmThresholdReached: (times: Int) -> Unit,
    private val distanceThresholdCm: Float
) : ScrollableState by lazyListState {

    private var scrolledItems by mutableIntStateOf(0)
    private var scrolledDistanceDp by mutableFloatStateOf(0f)
    private var scrolledDistanceCm by mutableFloatStateOf(0f)
    private var previousFirstVisibleItemIndex by mutableIntStateOf(-1)
    private var previousFirstItemOffset by mutableIntStateOf(0)

    init {
        snapshotFlow {
            Pair(
                lazyListState.firstVisibleItemIndex,
                lazyListState.layoutInfo
            )
        }.onEach { (currIndex, layoutInfo) ->
                if (previousFirstVisibleItemIndex >= 0) {
                    calculateScrolledDistance(layoutInfo)
                    checkThresholdCm()
                }
                previousFirstVisibleItemIndex = currIndex
                previousFirstItemOffset = layoutInfo.visibleItemsInfo.firstOrNull()?.offset ?: 0
            }.launchIn(scope)
    }

    private fun calculateScrolledDistance(layoutInfo: LazyListLayoutInfo) {
        val firstVisibleItemOffset = layoutInfo.visibleItemsInfo.firstOrNull()?.offset ?: 0
        val offsetChange = when (scrollDirection) {
            ScrollDirection.FORWARD -> previousFirstItemOffset - firstVisibleItemOffset
            ScrollDirection.BACKWARD -> firstVisibleItemOffset - previousFirstItemOffset
        }.coerceAtLeast(0) // Ensure it's non-negative


        if (offsetChange > 0) {
            val distanceDp = offsetChange / density
            scrolledDistanceDp += distanceDp
            val distanceInInches = distanceDp / density
            scrolledDistanceCm = distanceInInches * CM_PER_INCH

            //only notify if we have scrolled a whole dp
            if (scrolledDistanceDp.toInt().toFloat() == scrolledDistanceDp) {
                onItemScrolled(ScrollUnit.Dp(distanceDp.toInt()))
            }

            //only notify if we have scrolled a whole .1 cm TODO -> pass in granularity
            val decimalPart = (scrolledDistanceCm - scrolledDistanceCm.toInt()) * 100

            if (decimalPart.toInt() % 2 == 0) {
                onItemScrolled(ScrollUnit.Cm(scrolledDistanceCm))
            }
        }
    }

    private fun checkThresholdCm() {
        if ((scrolledDistanceCm % distanceThresholdCm) == 0f) {
            onCmThresholdReached((scrolledDistanceCm / distanceThresholdCm).toInt())
        }
    }
}

// Placeholder for conversion constant
private const val CM_PER_INCH = 2.54f