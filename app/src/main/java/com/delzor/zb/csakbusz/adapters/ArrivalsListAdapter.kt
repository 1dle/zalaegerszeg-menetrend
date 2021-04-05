package com.delzor.zb.csakbusz.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.delzor.zb.csakbusz.Arrival
import com.delzor.zb.csakbusz.R

class ArrivalsListAdapter(var itemList: MutableList<Arrival>) : RecyclerView.Adapter<CustomViewHolder>() {
    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val singleItem = layoutInflater.inflate(R.layout.list_item_arrivals, parent, false)
        return CustomViewHolder(singleItem)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val cnt = holder.view.context
        val arrival = itemList[position]
        holder.view.findViewById<TextView>(R.id.tvArrivalBusName).text = arrival.name
        val minute = arrival.time.toString().split(".")[0]
        //val sec = ((time[1].substring(0,2).toDouble()/100)*60).toInt() felesleges -> szar a szerver
        holder.view.findViewById<TextView>(R.id.tvArrivalBusTime).text = "Érkezés: ~$minute perc múlva"
        if (arrival.type != 1) {
            holder.view.findViewById<ImageView>(R.id.ivArrivalWheelchair).visibility = View.VISIBLE
        }


    }
}