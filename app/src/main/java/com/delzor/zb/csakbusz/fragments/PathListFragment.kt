package com.delzor.zb.csakbusz.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.delzor.zb.csakbusz.*
import com.delzor.zb.csakbusz.adapters.PathListAdapter
import com.delzor.zb.csakbusz.adapters.SimpleListAdapter
import com.delzor.zb.csakbusz.databinding.FragmentPathListBinding
import org.jetbrains.anko.alert
import org.jetbrains.anko.support.v4.act
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.yesButton

class PathListFragment: Fragment() {

    private var _binding: FragmentPathListBinding? = null
    private val binding get() = _binding!!

    var vizible = false
    var prevID = 0
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentPathListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        vizible = isVisibleToUser
        if(vizible){
            if(Data.selectedSubLine.stopSpots==null){
                activity!!.alert {
                    title = "Figyelem!"
                    message = "Kérlek válassz a listából egy útvonalat először"
                    yesButton { activity!!.findViewById<ViewPager>(R.id.container).setCurrentItem(0, true) }
                    onCancelled { activity!!.findViewById<ViewPager>(R.id.container).setCurrentItem(0, true) }
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
        activity!!.runOnUiThread{
            binding.rvPathList.layoutManager = LinearLayoutManager(context)
            binding.rvPathList.adapter = PathListAdapter(stopSpots, pathTime)

        }

    }
    fun fillStartTimesRV(){
        activity!!.runOnUiThread {
            binding.rvStartTimes.layoutManager = GridLayoutManager(context, 5)
            val times = Data.selectedSubLine.startTimes!!.map{
                "${String.format("%02d",it.first)}:${String.format("%02d",it.second)}"
            }.toMutableList()
            binding.rvStartTimes.adapter = SimpleListAdapter(
                    times
            )
        }
    }
}