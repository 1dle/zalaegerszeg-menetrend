package com.delzor.zb.csakbusz.adapters

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.delzor.zb.csakbusz.Data
import com.delzor.zb.csakbusz.Line
import com.delzor.zb.csakbusz.R
import com.delzor.zb.csakbusz.Utils
import kotlinx.android.synthetic.main.list_item_sublines.view.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.sdk25.coroutines.onClick

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

        holder.view.tvSublineName.text = line.name.replace("-", " - ")
        holder.view.ivSublineIcon.setImageBitmap(Utils.circularBG(cnt, Color.parseColor(Data.KOLORS(position))))

        holder.view.onClick {
            holder.view.backgroundColor = Color.parseColor(Data.KOLORS(position))
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