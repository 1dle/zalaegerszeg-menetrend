package com.delzor.zb.csakbusz.adapters

import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.delzor.zb.csakbusz.Data
import com.delzor.zb.csakbusz.Line
import com.delzor.zb.csakbusz.R
import com.delzor.zb.csakbusz.Utils

class LineListAdapter(var itemList: MutableList<Line>, val onItmClick: (Line) -> Unit) : RecyclerView.Adapter<CustomViewHolder>() {
    var prev: View? = null
    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val singleItem = layoutInflater.inflate(R.layout.list_item_sublines, parent, false)
        return CustomViewHolder(singleItem)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val cnt = holder.view.context
        val line = itemList[position]


        holder.view.findViewById<TextView>(R.id.tvSublineName).text = line.name.replace("-", " - ")
        holder.view.findViewById<ImageView>(R.id.ivSublineIcon).setImageBitmap(Utils.circularBG(cnt, Color.parseColor(Data.KOLORS(position))))

        holder.view.setOnClickListener {
            holder.view.setBackgroundColor(Color.parseColor(Data.KOLORS(position)))
            //Color.parseColor("#BBFF61")
            if (prev != null && prev != holder.view) {
                val outValue = TypedValue()
                cnt.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
                prev!!.setBackgroundResource(outValue.resourceId)
            }
            Data.selectedSubLine.pathColor = Data.KOLORS(position)
            prev = holder.view
            onItmClick(line)
        }
        if(itemCount == 1){
            //ha csak 1 van elem van a listában
            holder.view.performClick()
        }

    }
}