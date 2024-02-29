package com.ahugenb.bloomscroll.recycler

import android.view.View

sealed class BloomScrollItem(open val view: View) {
    abstract val shouldCount: Boolean

    data class ContentItem(override val view: View) : BloomScrollItem(view) {
        override val shouldCount: Boolean = true
    }

    data class DummyItem(override val view: View) : BloomScrollItem(view) {
        override val shouldCount: Boolean = false
    }
}