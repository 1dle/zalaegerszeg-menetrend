package com.delzor.zb.csakbusz.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.delzor.zb.csakbusz.Bus
import com.delzor.zb.csakbusz.Data
import com.delzor.zb.csakbusz.Utils.addZeroes
import com.delzor.zb.csakbusz.Data.RESP
import com.delzor.zb.csakbusz.R
import com.delzor.zb.csakbusz.Utils
import com.delzor.zb.csakbusz.adapters.TopBusListAdapter

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import okhttp3.*
import java.io.IOException
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import kotlinx.android.synthetic.main.activity_bus_view_map.*
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate


class BusViewMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var moveTo: Boolean = true
    private var currBusList: MutableList<Bus> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bus_view_map)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        rvScreenTopBusList.layoutManager = LinearLayoutManager(this)
    }

    override fun onStop() {

        super.onStop()

    }
    override fun onPause() {
        super.onPause()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.animateCamera(CameraUpdateFactory.zoomTo(13f))

        Data.timer = Timer()
        Data.timer.scheduleAtFixedRate(0,5000){
            runOnUiThread {
                currBusList.clear()
                fetchBusData{
                    when(it){
                        RESP.NODATA -> finish() // TODO:
                        RESP.ERROR  -> finish() // TODO:
                        RESP.SUCCESSFUL -> runOnUiThread { addBusMarkers() }
                    }

                }
            }
        }

    }
    fun fetchBusData(callback:(resp: Data.RESP) -> Unit){
        val extraBus = intent.extras.get("BUS").toString()
        title = extraBus
        val busz: String = addZeroes(extraBus)
        val url = "http://webgyor.kvrt.hu/csaoajax/sql-cs.php?vonal_irany=ZE$busz:O,V"
        // ZE001|O|VasĂştĂĄllomĂĄs-KovĂĄcs K.tĂŠr-AndrĂĄshida |46.84522250 |16.84416972 |2374|1

        var request = Request.Builder()
                .url(url)
                .build()
        Data.client.newCall(request).enqueue(object: Callback{
            override fun onFailure(call: Call?, e: IOException?) {

                //println(e)
                callback(Data.RESP.ERROR)
            }

            override fun onResponse(call: Call?, response: Response?) {
                val resp = response!!.body()!!.string().toString()
                if(resp.length < 2){
                    //nincs már ez a busz
                    callback(Data.RESP.NODATA)
                }else{
                    //print(resp)
                    var lines: MutableList<String> = resp!!.split("<br/>").toMutableList()

                    lines.removeAt(0) // Első sor tartalmazza a mezőneveket

                    for(item in lines){
                        val busdata = item.split("|")

                        val bus = Bus(
                                busdata[1].trim(), //irány : O
                                busdata[2].trim(), //név :   Vasutallomas - KK - stb.
                                busdata[0].trim(), //szám :  24
                                busdata[3].trim(), //lat :   46.546465
                                busdata[4].trim()  //lng :   16.34234
                        )
                        //println(bus.name)
                        currBusList.add(bus)
                        //val bus1 = Bus("ASD", "ASD", "8","46.84","16.84389") currBusList.add(bus1)

                    }
                    callback(Data.RESP.SUCCESSFUL)
                }

            }
        })
    }
    fun addBusMarkers(){
        mMap.clear()
        //BUSZOK HOZZÁADÁSA A MAPHOZ
        var busColor: Data.BUSCOLOR = Data.BUSCOLOR.YELLOW
        //var color = true // busz szine : sarga
        for (bus in currBusList){
            val pos = LatLng(bus.lat.toDouble(),bus.lng.toDouble())
            mMap.addMarker(MarkerOptions()
                    .position(pos)
                    .title(bus.name)
                    .icon(BitmapDescriptorFactory.fromBitmap(Utils.writeToBusIcon(applicationContext,
                            when (busColor) {
                                Data.BUSCOLOR.YELLOW -> R.drawable.bus_marker_magas
                                Data.BUSCOLOR.ORANGE -> R.drawable.bus_marker_magas2
                            },

                            bus.num))))
            busColor = busColor.getNext()
        }
        //Kameramozgás
        if(currBusList.size == 1){
            if (moveTo){
                val bus = currBusList.first()
                mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(bus.lat.toDouble(),bus.lng.toDouble())))
                mMap.animateCamera(CameraUpdateFactory.zoomTo(13f))
                moveTo = false
            }
        }else{
            // Kamera középre, ha két busz van
            val bus1 = currBusList.first()
            val bus2 = currBusList[1]
            val(lat, lng) = Utils.midPoint(bus1.lat.toDouble(), bus1.lng.toDouble(), bus2.lat.toDouble(), bus2.lng.toDouble())
            if(moveTo){
                mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(lat,lng)))
                mMap.animateCamera(CameraUpdateFactory.zoomTo(13f))
                moveTo = false
            }
        }

        runOnUiThread { rvScreenTopBusList.adapter = TopBusListAdapter(mMap, currBusList) }


    }
}
