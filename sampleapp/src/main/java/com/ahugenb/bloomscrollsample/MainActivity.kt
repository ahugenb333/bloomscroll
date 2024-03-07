package com.ahugenb.bloomscrollsample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.ahugenb.bloomscroll.lazylist.rememberLazyListWrapper
import com.ahugenb.bloomscroll.scroll.ScrollDirection
import com.ahugenb.bloomscroll.scroll.ScrollUnit


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    ScrollingWellnessTool() // Your composable function call
                }
            }
        }
    }
}

@Composable
fun ScrollingWellnessTool() {
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val timesReached = remember { mutableIntStateOf(0) }
    val distanceScrolledDp = remember { mutableIntStateOf(0) }
    val distanceScrolledCm = remember { mutableFloatStateOf(0f) }

    // Example usage of rememberLazyListWrapper
    val wrapper = rememberLazyListWrapper(
        lazyListState = lazyListState,
        scope = coroutineScope,
        scrollDirection = ScrollDirection.FORWARD,
        isDummyItem = { position -> false }, // Simplified: No dummy items in this example
        onItemScrolled = { unit ->
            when (unit) {
                is ScrollUnit.Dp -> distanceScrolledDp.intValue = unit.count
                is ScrollUnit.Cm -> distanceScrolledCm.floatValue = unit.distance
                else -> { /* TODO */ }
            }
        },
        onThresholdReached = { timesReached ->
            println("Threshold reached $timesReached times")
        },
        distanceThresholdCm = 15f // Example threshold
    )

    Column {
        Row {
            Text(text = "threshold: ${timesReached.intValue}, dp: ${distanceScrolledDp.intValue}, cm: ${distanceScrolledCm.floatValue}")
        }
        Row {
            LazyColumn(state = lazyListState, modifier = Modifier.fillMaxSize()) {
                items(1000) { index ->
                    Text(text = "Item #$index", modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}