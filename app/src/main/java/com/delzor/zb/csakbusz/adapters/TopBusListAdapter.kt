package com.delzor.zb.csakbusz.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.delzor.zb.csakbusz.Bus
import com.delzor.zb.csakbusz.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng

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

        holder.view.apply{
            findViewById<TextView>(R.id.tvBLname).text = bus.name
            findViewById<ImageView>(R.id.ivBLicon).setImageResource(
                    when (position) {
                        0 -> R.drawable.bus_marker
                        1 -> R.drawable.bus_marker2
                        else -> R.drawable.bus_marker // TODO
                    }
            )
            setOnClickListener {
                mMap.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                                LatLng(bus.lat.toDouble(), bus.lng.toDouble()),
                                18f
                        ))

            }

        }
    }


}