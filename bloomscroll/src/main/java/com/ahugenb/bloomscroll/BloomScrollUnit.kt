package com.ahugenb.bloomscroll

sealed class BloomScrollUnit {
    data class Item(val count: Int, val heightInDp: Int) : BloomScrollUnit()
    data class Dp(val count: Int) : BloomScrollUnit()
    data class Cm(val distance: Float) : BloomScrollUnit()
}