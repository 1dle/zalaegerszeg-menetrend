package com.delzor.zb.csakbusz.activities

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.delzor.zb.csakbusz.Data.currStopSpots
import com.delzor.zb.csakbusz.Data.fetchNearStopSpot
import com.delzor.zb.csakbusz.Data.fetchStopSpot
import com.delzor.zb.csakbusz.Data.nearStopSpots

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import android.R.string.cancel
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat
import com.delzor.zb.csakbusz.*
import org.jetbrains.anko.*
import java.lang.Exception


class BusStopsLocationMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    var nearStopsShow = true
    private val LOCATION_PERMISSION_CODE = 1
    private var locationManager1 : LocationManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bus_stops_location_map)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        /*
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                1)*/
        locationManager1 = getSystemService(LOCATION_SERVICE) as LocationManager?

    }
    private fun requestLocationPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
            alert("A jelenlegi pozíciód lekéréséhez engedélyezned kell az alkalmazásnak hogy hozzáférjen a helyadatokhoz."){
                title = "Engedély szükséges"
                yesButton {
                    ActivityCompat.requestPermissions(act, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_CODE)

                }
                noButton { cancel ; finish()}
            }.show().setCancelable(false)
        }else{
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_CODE)
        }
    }
    fun isTurnedOnLoc() {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            alert("A GPS ki van kapcsolva. A közeli megállók lekéréséhez engedélyezned kell. Engedélyezed?"){
                title = "GPS kikapcsolva"
                yesButton { cancel ; startActivityForResult(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS),2) }
                noButton { cancel; finish() }
            }.show().setCancelable(false)


        }else{
            //longToast("Letöltés...")
            addNearStops()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if(requestCode == 2){
            //longToast("Letöltés...")
            addNearStops()
        }else{
            super.onActivityResult(requestCode, resultCode, data)
        }

    }
    fun getMyLocation(callback:(Location?) -> Unit){
        try {
            // Request location updates
            if(android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT){
                locationManager1?.requestSingleUpdate(LocationManager.NETWORK_PROVIDER,
                        locationListener{
                            callback(it)
                        }, null
                )
            }else{
                locationManager1?.requestSingleUpdate(LocationManager.GPS_PROVIDER,
                        locationListener{
                            callback(it)
                        }, null
                )
            }

        } catch(ex: SecurityException) {
            callback(null)
        } catch (ex: Exception){
            callback(null)
        }
    }
    private fun locationListener(callback:(Location) -> Unit): LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            callback(location)
        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val busStopID = intent.extras!!.getString("BUS_STOP_ID","nope")
        nearStopsShow = intent.getBooleanExtra("NEAR",false)


        if(nearStopsShow){
            //közeli megállük letöltése
            //gmaps|16.818946|46.850585
            if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                isTurnedOnLoc()
            } else{
                requestLocationPermission()
            }
        }else{
            fetchStopSpot(busStopID){
                runOnUiThread{
                    addStopSpotMarkers(currStopSpots)
                }
            }
        }
        mMap.setOnMarkerClickListener { marker ->
            if(marker.title != "Saját pozícíó"){
                marker.hideInfoWindow()
                startActivity<StopDataActivity>(
                        "STOPSPOT_ID" to marker.title,
                        "NEAR" to nearStopsShow

                )
            }
            true
        }

    }
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_PERMISSION_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    isTurnedOnLoc()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    finish()
                }
                return
            }

        // Add other 'when' lines to check for other
        // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }
    /*
    private fun buildAlertMessageNoGps() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("A GPS ki van kapcsolva. A közeli megállók lekéréséhez engedélyezned kell. Engedélyezed?")
                .setCancelable(false)
                .setPositiveButton("Igen", DialogInterface.OnClickListener { _, _ -> startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)) })
                .setNegativeButton("Nem", DialogInterface.OnClickListener { dialog, _ -> dialog.cancel();finish() })
        val alert = builder.create()
        alert.show()
    }*/
    fun addNearStops(){
        val dialog = indeterminateProgressDialog("Adatok letöltése...\nEgy ideig eltarthat!")
        dialog.setCancelable(false)
        dialog.show()
        getMyLocation {
            var lat = Data.ZEG.latitude
            var lng = Data.ZEG.longitude
            if (it!=null){
                lat = it?.latitude?: Data.ZEG.latitude
                lng = it?.longitude?: Data.ZEG.longitude
            }
                    fetchNearStopSpot(lat.toString(),lng.toString()){
                        runOnUiThread {
                            mMap.addMarker(MarkerOptions().position(LatLng(lat,lng)).title("Saját pozícíó")) //Az én pozícióm hozzáadása
                            addStopSpotMarkers(nearStopSpots)
                            dialog.hide()
                            dialog.dismiss()
                        }
                    }

        }
        }
    fun addStopSpotMarkers(stopSpotList: MutableList<StopSpot>){
        for (stopspot in stopSpotList){
            val pos = LatLng(stopspot.lat.toDouble(),stopspot.lng.toDouble())
            mMap.addMarker(MarkerOptions()
                    .position(pos)
                    .title(stopspot.id)
                    .icon(BitmapDescriptorFactory.fromBitmap(Utils.writeToBusStopIcon(applicationContext,
                            R.drawable.bus_stop_icon,
                            stopspot.no))))

        }
        //Kameramozgás
        if(stopSpotList.size == 1){
                val bus = stopSpotList.first()
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(bus.lat.toDouble(),bus.lng.toDouble()),19f))

        }else{
            //ha több buszmeg van akkor a középsőre zooomol / pld.: Autóbusz-állomás
            val builder = LatLngBounds.Builder()
            stopSpotList.forEach{
                builder.include(LatLng(it.lat.toDouble(),it.lng.toDouble()))
            }
            val bounds = builder.build()

            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, if(nearStopsShow){100}else{150}))
        }
    }
}
