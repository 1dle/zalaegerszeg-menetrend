package com.delzor.zb.csakbusz.adapters

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.delzor.zb.csakbusz.R
import kotlinx.android.synthetic.main.list_item_text.view.*

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

        if (style && position % 2 != 0) {
            holder.view.setBackgroundColor(Color.parseColor("#E1E5E2"))
        }

        holder.view.text1.textSize = textSize
        holder.view.text1.text = text


    }
}