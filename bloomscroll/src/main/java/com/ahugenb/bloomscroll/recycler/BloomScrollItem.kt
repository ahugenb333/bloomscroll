package com.ahugenb.bloomscroll.recycler

import android.view.View

open class BloomScrollItem<T : View>(open val view: T, val shouldCount: Boolean)