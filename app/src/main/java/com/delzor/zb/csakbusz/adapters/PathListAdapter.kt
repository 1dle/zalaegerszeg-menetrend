package com.delzor.zb.csakbusz.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.delzor.zb.csakbusz.*

class PathListAdapter(var stopSpots: MutableList<StopSpot>, val pathTimes: MutableList<Int>) : RecyclerView.Adapter<CustomViewHolder>() {
    override fun getItemCount(): Int {
        return stopSpots.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val singleItem = layoutInflater.inflate(R.layout.list_item_pathlist, parent, false)
        return CustomViewHolder(singleItem)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val cnt = holder.view.context
        holder.setIsRecyclable(false)

        val busStop = stopSpots[position]
        val pathTime = pathTimes[position]

        holder.view.run {
            findViewById<TextView>(R.id.tvPathListName).text = busStop.name
            findViewById<TextView>(R.id.tvPathListTime).text = pathTime.toString()
            findViewById<ImageView>(R.id.ivPathListBullet).setImageBitmap(
                    Utils.bulletIcon(
                        when(position){
                            0 -> Data.BULLETPOS.FIRST
                            itemCount-1 -> Data.BULLETPOS.LAST
                            else -> Data.BULLETPOS.MID
                        }
                    )
            )
        }
    }
}