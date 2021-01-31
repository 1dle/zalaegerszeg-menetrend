package com.delzor.zb.csakbusz.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.delzor.zb.csakbusz.*
import com.delzor.zb.csakbusz.activities.BusStopsLocationMapActivity
import com.delzor.zb.csakbusz.activities.LineDataActivity
import com.delzor.zb.csakbusz.activities.StopDataActivity
import kotlinx.android.synthetic.main.list_item_busstop.view.*
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity

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
            holder.view.tvBusStopName.text = "${line.num} ${line.name.substring(2).replace("-"," - ")}"
            holder.view.ivStopOrBus.imageResource = R.drawable.bus_marker

            holder.view.onClick{
                Data._testLineNum = line.num
                cnt.startActivity<LineDataActivity>(
                        "TITLE" to line.name
                )
            }


        }else{
            val busStop = stopList!![position]
            holder.view.tvBusStopName.text = busStop.name

            holder.view.onClick {
                Data.fetchStopSpot(busStop.id) {
                    //println("SIZE:::: "+Data.currStopSpots.size)
                    if (Data.currStopSpots.size == 1) {
                        // ha csak 1 buszmeg van kihagyja a buszválasztó mapot
                        cnt.startActivity<StopDataActivity>("STOPSPOT_ID" to Data.currStopSpots[0].id)
                        //println("CSAK EGY")
                    } else {
                        cnt.startActivity<BusStopsLocationMapActivity>(
                                "BUS_STOP_ID" to busStop.id
                        )
                        //println("TÖBB")
                    }
                    Data._testStopSpotName = busStop.name
                }


            }
        }
    }
}