package com.ahugenb.bloomscroll.recycler

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView

class BloomScrollAdapter(private val items: List<BloomScrollItem<out View>>) : RecyclerView.Adapter<BloomScrollAdapter.BloomViewHolder>() {

    class BloomViewHolder(val container: FrameLayout) : RecyclerView.ViewHolder(container) {
        lateinit var item: BloomScrollItem<out View> // Add the item property
        val view: View // Expose the view directly
            get() = item.view
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BloomViewHolder {
        val frameLayout = FrameLayout(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        return BloomViewHolder(frameLayout)
    }

    override fun onBindViewHolder(holder: BloomViewHolder, position: Int) {
        val bloomItem = items[position] // Renamed for clarity
        holder.container.removeAllViews()
        holder.item = bloomItem  // Assign the BloomScrollItem
        if (bloomItem.shouldCount) {
            holder.container.addView(bloomItem.view)
        }
    }

    override fun getItemCount(): Int = items.size
}