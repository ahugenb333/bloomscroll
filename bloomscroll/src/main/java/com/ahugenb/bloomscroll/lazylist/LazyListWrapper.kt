package com.ahugenb.bloomscroll.lazylist

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class LazyListWrapper(
    private val lazyListState: LazyListState,
    private val scope: CoroutineScope,
    private val isDummyItem: (position: Int) -> Boolean,
    private val onScroll: (currentTotal: Int) -> Unit,
    private val onThresholdReached: (times: Int) -> Unit,
    private val itemThreshold: Int
) : ScrollableState by lazyListState {

    private var timesScrolled by mutableIntStateOf(0)
    private var previousIndex by mutableIntStateOf(0)
    private var totalItemsScrolled by mutableIntStateOf(0)

    init {
        snapshotFlow { Pair(lazyListState.firstVisibleItemScrollOffset, lazyListState.firstVisibleItemIndex) }
            .onEach { ( _, currentIndex) ->
                if (!isDummyItem(currentIndex)) {
                    totalItemsScrolled += currentIndex - previousIndex
                    previousIndex = currentIndex
                    onScroll(totalItemsScrolled)
                    if (totalItemsScrolled >= (timesScrolled + 1) * itemThreshold) {
                        timesScrolled++
                        onThresholdReached(timesScrolled)
                    }
                }
            }.launchIn(scope)
    }
}