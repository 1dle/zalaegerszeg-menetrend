package com.delzor.zb.csakbusz.adapters

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import com.delzor.zb.csakbusz.activities.BusViewMapActivity
import com.delzor.zb.csakbusz.R

class BuslistAdapter(var itemList: MutableList<String>) : RecyclerView.Adapter<CustomViewHolder>() {
    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val singleItem = layoutInflater.inflate(R.layout.list_item_button, parent, false)
        return CustomViewHolder(singleItem)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val cnt = holder.view.context
        val btnTxt = itemList[position]
        holder.view.findViewById<Button>(R.id.btnListItem).text = btnTxt

        holder.view.findViewById<Button>(R.id.btnListItem).setOnClickListener {
            Intent(cnt, BusViewMapActivity::class.java).apply {
                putExtra("BUS", btnTxt)
            }.also { cnt.startActivity(it) }

        }

    }
}