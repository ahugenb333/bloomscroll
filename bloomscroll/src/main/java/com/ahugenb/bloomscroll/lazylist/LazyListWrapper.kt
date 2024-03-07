package com.ahugenb.bloomscroll.lazylist

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import com.ahugenb.bloomscroll.scroll.ScrollDirection
import com.ahugenb.bloomscroll.scroll.ScrollUnit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class LazyListWrapper(
    private val lazyListState: LazyListState,
    private val scope: CoroutineScope,
    private val density: Float,
    private val scrollDirection: ScrollDirection = ScrollDirection.FORWARD,
    private val isDummyItem: (position: Int) -> Boolean,
    private val onItemScrolled: (ScrollUnit) -> Unit,
    private val onThresholdReached: (times: Int) -> Unit,
    private val distanceThresholdCm: Float
) : ScrollableState by lazyListState {

    private var previousOffset by mutableStateOf(0)
    private var scrolledDistanceCm by mutableStateOf(0f)
    private var thresholdCounter by mutableStateOf(0)

    init {
        snapshotFlow { lazyListState.firstVisibleItemScrollOffset }
            .onEach { currentOffset ->
                val deltaPx = if (previousOffset == 0) 0 else currentOffset - previousOffset
                previousOffset = currentOffset
                if (deltaPx != 0) {
                    updateScrolledDistance(deltaPx)
                }
            }.launchIn(scope)
    }

    private fun updateScrolledDistance(deltaPx: Int) {
        val scrolledDp = deltaPx / density
        scrolledDistanceCm += (scrolledDp / 160) * CM_PER_INCH // Correctly converting px to cm

        onItemScrolled(ScrollUnit.Dp(scrolledDp.toInt()))
        onItemScrolled(ScrollUnit.Cm(scrolledDistanceCm))

        checkThresholdCm()
    }

    private fun checkThresholdCm() {
        while (scrolledDistanceCm >= (thresholdCounter + 1) * distanceThresholdCm) {
            thresholdCounter++
            onThresholdReached(thresholdCounter)
        }
    }

    companion object {
        private const val CM_PER_INCH = 2.54f
    }
}