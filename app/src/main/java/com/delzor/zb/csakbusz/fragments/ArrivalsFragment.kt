package com.delzor.zb.csakbusz.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.delzor.zb.csakbusz.*
import com.delzor.zb.csakbusz.adapters.ArrivalsListAdapter
import kotlinx.android.synthetic.main.fragment_arrivals.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import org.jetbrains.anko.support.v4.act
import java.io.IOException

class ArrivalsFragment: Fragment(){

    var currArrivals = mutableListOf<Arrival>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
        = inflater.inflate(R.layout.fragment_arrivals, container, false)


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        fetchStopData {
            when(it){
                Data.RESP.SUCCESSFUL -> act.runOnUiThread{
                    rvArrivals1.layoutManager = LinearLayoutManager(context)
                    rvArrivals1.adapter = ArrivalsListAdapter(currArrivals)
                    tvArrivalsMsg.visibility = View.GONE
                }
                Data.RESP.NODATA -> act.runOnUiThread{
                    tvArrivalsMsg.text = "Nem érkezik busz a következő órában."
                    tvArrivalsMsg.visibility = View.VISIBLE
                }
                Data.RESP.ERROR -> act.runOnUiThread{
                    tvArrivalsMsg.text = "Hiba. Nem sikerült csatlakozni a szerverhez"
                    tvArrivalsMsg.visibility = View.VISIBLE
                }
            }
            Data.numOfDownloadedPages++


        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun fetchStopData(callback:(Data.RESP) -> Unit){

        val url = "http://zalaegerszeg.enykk.hu/php/sql.php?id=online_erkezes|"+Data._testStopSpot

        var request = Request.Builder()
                .url(url)
                .build()

        Data.client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                callback(Data.RESP.ERROR)
            }

            override fun onResponse(call: Call?, response: Response?) {
                var resp = response!!.body()!!.string().toString()
                //resp = Jsoup.parse(resp).text()
                if (resp.length < 2){
                    callback(Data.RESP.NODATA)
                }else{
                    var lines: MutableList<String> = resp!!.split("\n").toMutableList()

                    lines.removeAt(0) // Első sor tartalmazza a mezőneveket

                    for(line in lines){
                        var items = line.split("|")

                        val a = Arrival(
                                items[2].trim(),
                                items[7].toInt(),
                                items[3].toDouble()
                        )
                        currArrivals.add(a)
                    }

                    callback(Data.RESP.SUCCESSFUL)

                }

            }
        }
        )

    }
}