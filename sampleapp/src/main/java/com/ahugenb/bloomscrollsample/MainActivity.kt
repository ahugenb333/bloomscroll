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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.ahugenb.bloomscroll.lazylist.rememberLazyListWrapper


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
    val itemsScrolled = remember { mutableIntStateOf(0) }

    // Example usage of rememberLazyListWrapper
    val wrapper = rememberLazyListWrapper(
        lazyListState = lazyListState,
        scope = coroutineScope,
        isDummyItem = { _ -> false },
        onScroll =  { currentTotal ->
            itemsScrolled.intValue = currentTotal
        },
        onThresholdReached = { times ->
            timesReached.intValue = times
        },
        30
    )

    Column {
        Row {
            Text(fontSize = 24.sp, text = "threshold hit: ${timesReached.intValue}, items scrolled: ${itemsScrolled.intValue}")
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