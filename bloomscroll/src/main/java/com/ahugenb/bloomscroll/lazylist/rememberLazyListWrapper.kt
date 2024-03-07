package com.ahugenb.bloomscroll.lazylist

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalDensity
import com.ahugenb.bloomscroll.scroll.ScrollDirection
import com.ahugenb.bloomscroll.scroll.ScrollUnit
import kotlinx.coroutines.CoroutineScope

@Composable
fun rememberLazyListWrapper(
    lazyListState: LazyListState = rememberLazyListState(),
    scope: CoroutineScope = rememberCoroutineScope(),
    scrollDirection: ScrollDirection = ScrollDirection.FORWARD,
    isDummyItem: (position: Int) -> Boolean,
    onItemScrolled: (unit: ScrollUnit) -> Unit,
    onThresholdReached: (times: Int) -> Unit,
    distanceThresholdCm: Float,
): LazyListWrapper {
    val density = LocalDensity.current.density
    return remember(lazyListState, scope, density) {
        LazyListWrapper(
            lazyListState,
            scope,
            density,
            scrollDirection,
            isDummyItem,
            onItemScrolled,
            onThresholdReached,
            distanceThresholdCm
        )
    }
}