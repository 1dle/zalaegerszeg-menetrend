package com.delzor.zb.csakbusz.adapters

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.delzor.zb.csakbusz.*
import com.delzor.zb.csakbusz.activities.BusStopsLocationMapActivity
import com.delzor.zb.csakbusz.activities.LineDataActivity
import com.delzor.zb.csakbusz.activities.StopDataActivity

class BusStopLineListAdapter(var stopList: MutableList<BusStop>? = null, var  lineList: MutableList<SimpleLine>? = null) : RecyclerView.Adapter<CustomViewHolder>() {
    val lines = lineList != null
    override fun getItemCount(): Int {
        return if(lines)lineList!!.size else stopList!!.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val singleItem = layoutInflater.inflate(R.layout.list_item_busstop, parent, false)
        return CustomViewHolder(singleItem)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val cnt = holder.view.context
        if(lines){
            val line = lineList!![position]
            holder.view.findViewById<TextView>(R.id.tvBusStopName).text = "${line.num} ${line.name.substring(2).replace("-"," - ")}"
            holder.view.findViewById<ImageView>(R.id.ivStopOrBus).setImageResource(R.drawable.bus_marker)

            holder.view.setOnClickListener{
                Data._testLineNum = line.num
                Intent(cnt, LineDataActivity::class.java).apply {
                    putExtra("TITLE", line.name)
                }.also { cnt.startActivity(it) }
            }


        }else{
            val busStop = stopList!![position]
            holder.view.findViewById<TextView>(R.id.tvBusStopName).text = busStop.name

            holder.view.setOnClickListener {
                Data.fetchStopSpot(busStop.id) {
                    //println("SIZE:::: "+Data.currStopSpots.size)
                    if (Data.currStopSpots.size == 1) {
                        // ha csak 1 buszmeg van kihagyja a buszválasztó mapot
                        Intent(cnt, StopDataActivity::class.java).apply{
                            putExtra("STOPSPOT_ID", Data.currStopSpots[0].id)
                        }.also { cnt.startActivity(it) }

                        //println("CSAK EGY")
                    } else {
                        Intent(cnt, BusStopsLocationMapActivity::class.java).apply{
                            putExtra("BUS_STOP_ID", busStop.id)
                        }.also { cnt.startActivity(it) }
                        //println("TÖBB")
                    }
                    Data._testStopSpotName = busStop.name
                }


            }
        }
    }
}