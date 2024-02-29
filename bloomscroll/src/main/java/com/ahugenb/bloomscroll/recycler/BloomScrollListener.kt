package com.ahugenb.bloomscroll.recycler

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ahugenb.bloomscroll.BloomScrollUnit
import com.ahugenb.bloomscroll.BloomScrollUtils.Companion.toCm

class BloomScrollListener(
    private val context: Context,
    private val onThresholdReached: () -> Unit,
    private val onScrollIncrement: (unit: BloomScrollUnit, cumulativeTotal: Float) -> Unit
) : RecyclerView.OnScrollListener() {

    private var cumulativeTotal = 0f  // Track the total in centimeters
    private var thresholdUnit: BloomScrollUnit? = null

    fun setThreshold(unit: BloomScrollUnit) {
        thresholdUnit = unit
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)

        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            val layoutManager = recyclerView.layoutManager!!
            val firstVisiblePosition = when (layoutManager) {
                is LinearLayoutManager -> layoutManager.findFirstVisibleItemPosition()
                // Add support for other LayoutManagers if needed...
                else -> -1
            }

            if (firstVisiblePosition != RecyclerView.NO_POSITION) {
                for (i in firstVisiblePosition..(layoutManager as LinearLayoutManager).findLastVisibleItemPosition()) {
                    val adapterItem = recyclerView.adapter?.getItemViewType(i)
                    val holder = recyclerView.findViewHolderForAdapterPosition(i) as? BloomScrollAdapter.BloomViewHolder

                    if (holder != null && adapterItem != null && holder.item.shouldCount) {
                        val calculatedScrollUnit = calculateViewScroll(holder.view, context)
                        cumulativeTotal += calculateCumulativeTotal(calculatedScrollUnit)
                        onScrollIncrement(calculatedScrollUnit, cumulativeTotal)

                        if (thresholdUnit != null && shouldNotifyThreshold(cumulativeTotal, calculatedScrollUnit)) {
                            onThresholdReached()
                            // Optional: Reset cumulativeTotal if needed
                            // cumulativeTotal = 0f
                        }
                    }
                }
            }
        }
    }

    private fun <T : View> calculateViewScroll(view: T, context: Context): BloomScrollUnit {
        val recyclerViewTop = 0 // Adjust if needed
        val pixelsScrolledPastTop = maxOf(0f, (recyclerViewTop - view.top).toFloat())

        return when {
            view.height > pixelsScrolledPastTop -> BloomScrollUnit.Item(1, view.height / context.resources.displayMetrics.density.toInt())
            else -> BloomScrollUnit.Cm(pixelsScrolledPastTop / context.resources.displayMetrics.densityDpi * 2.54f) // Calculate Cm directly
        }
    }

    private fun calculateCumulativeTotal(unit: BloomScrollUnit): Float {
        when (unit) {
            is BloomScrollUnit.Item -> { /* No action needed - count-based approach */ }
            is BloomScrollUnit.Cm -> cumulativeTotal += unit.distance
            is BloomScrollUnit.Dp -> cumulativeTotal += unit.toCm(context).distance
        }
        return cumulativeTotal
    }

    private fun shouldNotifyThreshold(cumulativeTotal: Float, originalUnit: BloomScrollUnit): Boolean {
        val thresholdUnitAsCm = originalUnit.toCm(context) // Convert threshold to centimeters
        return cumulativeTotal >= thresholdUnitAsCm.distance
    }
}