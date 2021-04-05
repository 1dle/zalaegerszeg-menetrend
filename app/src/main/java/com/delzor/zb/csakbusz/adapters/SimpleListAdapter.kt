package com.delzor.zb.csakbusz.adapters

import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.delzor.zb.csakbusz.R

class SimpleListAdapter(var itemList: MutableList<String>, var textSize: Float = 14f, var style: Boolean = false) : RecyclerView.Adapter<CustomViewHolder>() {
    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val singleItem = layoutInflater.inflate(R.layout.list_item_text, parent, false)
        return CustomViewHolder(singleItem)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val cnt = holder.view.context
        val text = itemList[position]

        holder.view.run{
            if (style && position % 2 != 0)
                setBackgroundColor(Color.parseColor("#E1E5E2"))
            findViewById<TextView>(R.id.text1).also{
                it.textSize = textSize
                it.text = text
            }
        }
    }
}