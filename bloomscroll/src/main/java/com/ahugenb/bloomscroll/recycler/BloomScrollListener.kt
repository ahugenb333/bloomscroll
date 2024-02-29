package com.ahugenb.bloomscroll.recycler

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ahugenb.bloomscroll.BloomScrollUnit
import com.ahugenb.bloomscroll.BloomScrollUtils.Companion.toItem

class BloomScrollListener(
    private val context: Context,
    private val onThresholdReached: () -> Unit,
    private val onScrollIncrement: (unit: BloomScrollUnit, cumulativeTotal: Float) -> Unit
) : RecyclerView.OnScrollListener() {

    private var totalScrolledDistanceInPixels = 0
    private var totalScrolledItems = 0
    private var lastVisibleItemPosition = 0
    private var thresholdUnit: BloomScrollUnit? = null

    fun setThreshold(unit: BloomScrollUnit) {
        thresholdUnit = unit
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        // Update total pixels scrolled
        totalScrolledDistanceInPixels += dy

        // Track the number of items scrolled
        val currentFirstVisible = recyclerView.layoutManager?.findFirstVisibleItemPosition() ?: 0
        if (currentFirstVisible > lastVisibleItemPosition) {
            totalScrolledItems += currentFirstVisible - lastVisibleItemPosition
            lastVisibleItemPosition = currentFirstVisible
        }

        // Process threshold and notifications
        thresholdUnit?.let { unit ->
            val cumulativeTotal = calculateCumulativeTotal(unit)
            if (shouldNotifyThreshold(cumulativeTotal, unit)) {
                onThresholdReached()
            }
            onScrollIncrement(unit, cumulativeTotal)
        }
    }

    private fun calculateCumulativeTotal(unit: BloomScrollUnit): Float {
        return when (unit) {
            is BloomScrollUnit.Item -> totalScrolledItems.toFloat()
            is BloomScrollUnit.Cm -> totalScrolledDistanceInPixels / context.resources.displayMetrics.densityDpi / 2.54f
            is BloomScrollUnit.Dp -> totalScrolledDistanceInPixels / (context.resources.displayMetrics.densityDpi / 160f)
        }
    }

    private fun shouldNotifyThreshold(cumulativeTotal: Float, originalUnit: BloomScrollUnit): Boolean {
        // Convert the threshold unit into an Item for consistent comparison
        val thresholdUnitAsItem = originalUnit.toItem(context)

        return cumulativeTotal >= thresholdUnitAsItem.count
    }
}
// Assuming LinearLayoutManager or GridLayoutManager for simplicity
fun RecyclerView.LayoutManager?.findFirstVisibleItemPosition(): Int =
    (this as? LinearLayoutManager)?.findFirstVisibleItemPosition() ?: 0