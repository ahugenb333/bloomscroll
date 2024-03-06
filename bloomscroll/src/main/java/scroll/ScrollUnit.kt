package scroll


sealed class ScrollUnit {
    data class Item(val index: Int, val count: Int, val heightInDp: Int) : ScrollUnit()
    data class Dp(val count: Int) : ScrollUnit()
    data class Cm(val distance: Float) : ScrollUnit()
}