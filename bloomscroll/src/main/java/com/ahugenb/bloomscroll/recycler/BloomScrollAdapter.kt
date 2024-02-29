package com.ahugenb.bloomscroll.recycler

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView

class BloomScrollAdapter(private val items: List<BloomScrollItem>) : RecyclerView.Adapter<BloomScrollAdapter.BloomViewHolder>() {

    class BloomViewHolder(val container: FrameLayout) : RecyclerView.ViewHolder(container)

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
        val item = items[position]
        holder.container.removeAllViews()
        if (item.shouldCount) {
            holder.container.addView(item.view)
        }
    }

    override fun getItemCount(): Int = items.size
}