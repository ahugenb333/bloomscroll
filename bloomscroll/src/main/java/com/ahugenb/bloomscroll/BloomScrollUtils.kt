package com.ahugenb.bloomscroll

import android.content.Context

class BloomScrollUtils {
    companion object {

        // Converts any BloomScrollUnit to Item, assuming direct conversion for simplicity
        fun BloomScrollUnit.toItem(
            context: Context? = null,
            itemCount: Int = 1
        ): BloomScrollUnit.Item = when (this) {
            is BloomScrollUnit.Item -> this // Already an Item
            is BloomScrollUnit.Dp -> {
                // Directly use count as heightInDp, assuming 1:1 conversion for simplicity
                BloomScrollUnit.Item(count = itemCount, heightInDp = this.count)
            }

            is BloomScrollUnit.Cm -> {
                // Convert Cm to Dp, requires context for screen density
                context?.let {
                    val metrics = it.resources.displayMetrics
                    val cmToPixels = (this.distance * metrics.xdpi / 2.54f).toInt()
                    val pixelsToDp = cmToPixels / metrics.density
                    BloomScrollUnit.Item(count = itemCount, heightInDp = pixelsToDp.toInt())
                }
                    ?: throw IllegalArgumentException("Context is required for converting Cm to Item.")
            }
        }

        fun BloomScrollUnit.toDp(context: Context? = null): BloomScrollUnit.Dp = when (this) {
            is BloomScrollUnit.Dp -> this // Already in Dp
            is BloomScrollUnit.Cm -> {
                // Convert Cm to Dp, requires context for screen density
                context?.let {
                    val metrics = it.resources.displayMetrics
                    val cmToPixels = (this.distance * metrics.xdpi / 2.54f).toInt()
                    val pixelsToDp = cmToPixels / metrics.density
                    BloomScrollUnit.Dp(pixelsToDp.toInt())
                } ?: throw IllegalArgumentException("Context is required for converting Cm to Dp.")
            }

            is BloomScrollUnit.Item -> BloomScrollUnit.Dp(this.heightInDp) // Use heightInDp directly
        }

        fun BloomScrollUnit.toCm(context: Context): BloomScrollUnit.Cm = when (this) {
            is BloomScrollUnit.Cm -> this // Already in Cm, return as is.
            is BloomScrollUnit.Dp -> {
                // Convert Dp to Cm, using non-nullable context for screen density
                val metrics = context.resources.displayMetrics
                val dpToPixels = this.count * metrics.density
                val pixelsToCm = dpToPixels / metrics.xdpi * 2.54f
                BloomScrollUnit.Cm(pixelsToCm)
            }

            is BloomScrollUnit.Item -> {
                // Convert Item height from Dp to Cm, similar to Dp to Cm conversion logic
                val metrics = context.resources.displayMetrics
                val dpToPixels = this.heightInDp * metrics.density
                val pixelsToCm = dpToPixels / metrics.xdpi * 2.54f
                BloomScrollUnit.Cm(pixelsToCm)
            }
        }
    }
}