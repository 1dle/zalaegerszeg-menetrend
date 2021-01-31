package com.delzor.zb.csakbusz.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.delzor.zb.csakbusz.*
import kotlinx.android.synthetic.main.list_item_pathlist.view.*

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

        holder.view.tvPathListName.text = busStop.name
        holder.view.tvPathListTime.text = pathTime.toString()
        val bltPos = when(position){
            0 -> Data.BULLETPOS.FIRST
            itemCount-1 -> Data.BULLETPOS.LAST
            else -> Data.BULLETPOS.MID
        }
        holder.view.ivPathListBullet.setImageBitmap(Utils.bulletIcon(cnt, bltPos))


    }
}