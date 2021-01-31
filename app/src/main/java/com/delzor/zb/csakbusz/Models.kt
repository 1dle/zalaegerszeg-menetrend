package com.delzor.zb.csakbusz

import android.graphics.Color
import com.google.android.gms.maps.model.LatLng

class Bus(val dir: String, var name: String, var num: String, var lat: String, var lng: String){
    init {
        name = name.replace("-"," ➜ ")

        num = Utils.removeZeroes(num.removePrefix("ZE"))
    }
}
class BusStop(val id: String, val name: String)
class StopSpot(val no: String, val id: String, val lat: String, val lng: String, val name: String? = null)
class Arrival(val name: String, val type: Int, val time: Double)
class TimeTable(val times: MutableList<String>, val lineName: String, val terminus: String)
class SimpleLine(val num: String, val name: String)//Az összes kilistázásánál kell...
class Line(val id: Int, val num: String, val name: String)
class Subline(var id: Int? = null,
              var pathtime: MutableList<Int>? = null,
              var startTimes: MutableList<Pair<Int,Int>>? = null,
              var stopSpots: MutableList<StopSpot>? = null,
              var path: MutableList<LatLng>? = null,
              var pathColor: String? = null)
