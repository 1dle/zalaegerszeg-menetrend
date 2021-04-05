package com.delzor.zb.csakbusz.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.delzor.zb.csakbusz.Data
import com.delzor.zb.csakbusz.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.yesButton


class PathMapFragment : Fragment(), OnMapReadyCallback {

    lateinit var mMap: GoogleMap
    var vizible = false
    var prevID = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var v = inflater.inflate(R.layout.fragment_path_map, container, false)
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapPath) as SupportMapFragment
        mapFragment.getMapAsync(this)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        //mMap.addMarker(MarkerOptions().position(zeg).title("ZEG"))

    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        vizible = isVisibleToUser
        vizible = isVisibleToUser
        if (vizible) {
            if (Data.selectedSubLine.stopSpots == null) {
                activity!!.alert {
                    title = "Figyelem!"
                    message = "Kérlek válassz a listából egy útvonalat először"
                    yesButton { activity!!.findViewById<ViewPager>(R.id.container).setCurrentItem(0, true) }
                    onCancelled { activity!!.findViewById<ViewPager>(R.id.container).setCurrentItem(0, true) }
                }.show()
            } else if (prevID != Data.selectedSubLine.id!!) {
                addPath()
                addMarkers()
                prevID = Data.selectedSubLine.id!!
            }
        }

    }

    fun addPath() {
        if(Data.selectedSubLine.path!=null){
            activity!!.runOnUiThread {
                mMap.clear()
                mMap.addPolyline(
                        PolylineOptions()
                                .addAll(Data.selectedSubLine.path!!)
                                .width(5f)
                                .color(Color.parseColor(Data.selectedSubLine.pathColor!!)))

            }
        }else{
            /*
            act!!.runOnUiThread{
                alert {
                    title = "Hiba!"
                    message = "Az útvonalat nem sikerült hozzáadni."
                    yesButton{}
                }.show()
            }
            */

        }

    }

    fun addMarkers() {

        val pm = BitmapFactory.decodeResource(context!!.resources, R.drawable.bus_stop_icon)
        val scale = 0.75f
        val smallmarker = Bitmap.createScaledBitmap(pm, (pm.width * scale).toInt(), (pm.height * scale).toInt(), false)
        activity!!.runOnUiThread {
            val builder = LatLngBounds.Builder()
            Data.selectedSubLine.stopSpots!!.forEach {
                val poz = LatLng(it.lat.toDouble(), it.lng.toDouble())
                mMap.addMarker(MarkerOptions()
                        .position(poz)
                        .icon(
                                BitmapDescriptorFactory.fromBitmap(smallmarker)
                        )
                        .title("${it.name} - ${it.no}")
                )
                builder.include(poz)

            }
            val bounds = builder.build()
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 110))
            prevID = Data.selectedSubLine.id!!
            //println("HOZZÁADVA")

        }

    }
}

