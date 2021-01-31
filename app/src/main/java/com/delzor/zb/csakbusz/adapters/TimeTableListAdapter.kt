package com.delzor.zb.csakbusz.adapters

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.delzor.zb.csakbusz.R
import com.delzor.zb.csakbusz.TimeTable
import kotlinx.android.synthetic.main.list_item_timetable.view.*

class TimeTableListAdapter(var itemList: MutableList<TimeTable>) : RecyclerView.Adapter<CustomViewHolder>() {
    var prev = ""
    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val singleItem = layoutInflater.inflate(R.layout.list_item_timetable, parent, false)
        return CustomViewHolder(singleItem)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val cnt = holder.view.context
        val tt = itemList[position]

        holder.view.tvTimetableLine.text = tt.lineName
        if (position == 0 || prev != tt.terminus) {
            holder.view.tvTimetableTerminus.text = tt.terminus
            holder.view.tvTimetableTerminus.visibility = View.VISIBLE
        } else {
            holder.view.tvTimetableTerminus.visibility = View.GONE
        }
        // Ha az utolsó ::
        if (itemList.size - 1 == position && prev != tt.terminus) {
            holder.view.tvTimetableTerminus.text = tt.terminus
            holder.view.tvTimetableTerminus.visibility = View.VISIBLE
        }
        prev = tt.terminus

        holder.view.rvSubTimeTable.layoutManager = GridLayoutManager(cnt, 5)
        holder.view.rvSubTimeTable.adapter = SimpleListAdapter(tt.times)

    }
}