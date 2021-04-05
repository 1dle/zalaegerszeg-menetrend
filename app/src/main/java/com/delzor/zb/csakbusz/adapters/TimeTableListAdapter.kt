package com.delzor.zb.csakbusz.adapters

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.delzor.zb.csakbusz.R
import com.delzor.zb.csakbusz.TimeTable

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

        holder.view.apply{
            findViewById<TextView>(R.id.tvTimetableLine).text = tt.lineName

            findViewById<TextView>(R.id.tvTimetableTerminus).let{
                if( (position == 0 || prev != tt.terminus) || //ha első
                    (itemList.size - 1 == position && prev != tt.terminus) //ha utolsó
                        ){
                    it.text = tt.terminus
                    it.visibility = View.VISIBLE
                }else{
                    it.visibility = View.GONE
                }
            }
            findViewById<RecyclerView>(R.id.rvSubTimeTable).apply{
                layoutManager = GridLayoutManager(cnt, 5)
                adapter = SimpleListAdapter(tt.times)
            }

        }
        prev = tt.terminus

    }
}