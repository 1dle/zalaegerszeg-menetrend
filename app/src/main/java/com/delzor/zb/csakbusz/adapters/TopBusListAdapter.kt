package com.delzor.zb.csakbusz.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.delzor.zb.csakbusz.Bus
import com.delzor.zb.csakbusz.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.list_item_topbus.view.*
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.sdk25.coroutines.onClick

class TopBusListAdapter(val mMap: GoogleMap, var itemList: List<Bus>) : RecyclerView.Adapter<CustomViewHolder>() {
    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val singleItem = layoutInflater.inflate(R.layout.list_item_topbus, parent, false)
        return CustomViewHolder(singleItem)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val cnt = holder.view.context

        val bus = itemList[position]

        holder.view.tvBLname.text = bus.name
        holder.view.ivBLicon.imageResource =
                when (position) {
                    0 -> R.drawable.bus_marker
                    1 -> R.drawable.bus_marker2
                    else -> R.drawable.bus_marker // TODO
                }
        holder.view.onClick {
            mMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                            LatLng(bus.lat.toDouble(), bus.lng.toDouble()),
                            18f
                    ))

        }
    }


}