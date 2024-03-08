package com.ahugenb.bloomscroll.lazylist

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope

@Composable
fun rememberLazyListWrapper(
    lazyListState: LazyListState = rememberLazyListState(),
    scope: CoroutineScope = rememberCoroutineScope(),
    isDummyItem: (position: Int) -> Boolean,
    onScroll: (Int) -> Unit,
    onThresholdReached: (times: Int) -> Unit,
    itemThreshold: Int
): LazyListWrapper {
    return remember(lazyListState, scope) {
        LazyListWrapper(
            lazyListState,
            scope,
            isDummyItem,
            onScroll,
            onThresholdReached,
            itemThreshold,
        )
    }
}