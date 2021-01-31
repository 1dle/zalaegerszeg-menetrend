package com.delzor.zb.csakbusz.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.delzor.zb.csakbusz.*
import com.delzor.zb.csakbusz.adapters.PathListAdapter
import com.delzor.zb.csakbusz.adapters.SimpleListAdapter
import kotlinx.android.synthetic.main.activity_line_data.*
import kotlinx.android.synthetic.main.fragment_path_list.*
import org.jetbrains.anko.support.v4.act
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.yesButton

class PathListFragment: Fragment() {
    var vizible = false
    var prevID = 0
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_path_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        vizible = isVisibleToUser
        if(vizible){
            if(Data.selectedSubLine.stopSpots==null){
                alert {
                    title = "Figyelem!"
                    message = "Kérlek válassz a listából egy útvonalat először"
                    yesButton { act.container.setCurrentItem(0, true) }
                    onCancelled { act.container.setCurrentItem(0, true) }
                }.show()
            }else if (prevID!= Data.selectedSubLine.id!!){
                fillPathTimeRV()
                fillStartTimesRV()
                prevID = Data.selectedSubLine.id!!
            }
        }

    }
    fun fillPathTimeRV(){
        //egy item hozzáadása fejlécnek
        val stopSpots = Data.selectedSubLine.stopSpots!!
        val pathTime = Data.selectedSubLine.pathtime!!
        act!!.runOnUiThread{
            rvPathList.layoutManager = LinearLayoutManager(context)
            rvPathList.adapter = PathListAdapter(stopSpots, pathTime)

        }

    }
    fun fillStartTimesRV(){
        act!!.runOnUiThread {
            rvStartTimes.layoutManager = GridLayoutManager(context,5)
            val times = Data.selectedSubLine.startTimes!!.map{
                "${String.format("%02d",it.first)}:${String.format("%02d",it.second)}"
            }.toMutableList()
            rvStartTimes.adapter = SimpleListAdapter(
                    times
            )
        }
    }
}