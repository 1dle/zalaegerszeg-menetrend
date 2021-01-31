package com.delzor.zb.csakbusz.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.delzor.zb.csakbusz.activities.BusViewMapActivity
import com.delzor.zb.csakbusz.R
import kotlinx.android.synthetic.main.list_item_button.view.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity

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
        holder.view.btnListItem.text = btnTxt

        holder.view.btnListItem.onClick {
            //cnt.toast(btnTxt)
            /*val i: Intent = Intent(cnt, BusViewMapActivity::class.java)
            i.putExtra("BUS",btnTxt)
            cnt.startActivity(i)*/
            cnt.startActivity<BusViewMapActivity>("BUS" to btnTxt)


        }

    }
}