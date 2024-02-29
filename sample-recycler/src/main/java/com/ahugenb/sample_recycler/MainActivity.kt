package com.ahugenb.sample_recycler

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ahugenb.bloomscroll.BloomScrollUnit
import com.ahugenb.bloomscroll.recycler.BloomScrollAdapter
import com.ahugenb.bloomscroll.recycler.BloomScrollItem
import com.ahugenb.bloomscroll.recycler.BloomScrollListener
import com.ahugenb.sample_recylcer.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tv: TextView = findViewById(R.id.callback_counter)
        val recyclerView: RecyclerView = findViewById(R.id.sample_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val sampleData = createSampleData(this)
        val adapter = BloomScrollAdapter(sampleData)
        recyclerView.adapter = adapter

        val bloomScrollListener = BloomScrollListener(
            this,
            onThresholdReached = {
                Toast.makeText(this, "threshold reached!", Toast.LENGTH_SHORT).show()
            },
            onScrollIncrement = { _, total ->
                tv.text = "callbacks: $total"
            }
        )
        recyclerView.addOnScrollListener(bloomScrollListener)

        // Example initial threshold
        bloomScrollListener.setThreshold(BloomScrollUnit.Cm(15.0f))
    }

    private fun createSampleData(context: Context): List<BloomScrollItem<out View>> {
        val items = mutableListOf<BloomScrollItem<out View>>()

        // Content Items (with some not counting)
        for (i in 0..100) {
            val textView = TextView(context).apply { text = "Content Item $i" }
            items.add(BloomScrollItem(textView, true))  // Pass shouldCount directly
        }
        for (i in 101..200) {
            val textView = TextView(context).apply { text = "Dummy Item $i" }
            items.add(BloomScrollItem(textView, false))  // Pass shouldCount directly
        }
        for (i in 201..300) {
            val textView = TextView(context).apply { text = "Content Item $i" }
            items.add(BloomScrollItem(textView, true))  // Pass shouldCount directly
        }

        return items
    }
}