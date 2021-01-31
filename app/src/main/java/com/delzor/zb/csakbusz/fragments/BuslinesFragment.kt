package com.delzor.zb.csakbusz.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.delzor.zb.csakbusz.*
import com.delzor.zb.csakbusz.adapters.SimpleListAdapter
import kotlinx.android.synthetic.main.fragment_buslines.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import org.jetbrains.anko.support.v4.act
import java.io.IOException

class BuslinesFragment : Fragment() {



    var currBusLines = mutableListOf<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_buslines, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        fetchBusLinesFData{
            act.runOnUiThread {
                rvBuslinesFragment.layoutManager = LinearLayoutManager(context)
                rvBuslinesFragment.adapter = SimpleListAdapter(currBusLines, 16f, true)
            }
            Data.numOfDownloadedPages++
        }
    }

    fun fetchBusLinesFData( callback: () -> Unit) {

        val url = "http://zalaegerszeg.enykk.hu/php/sql.php?id=kocsiall_vonalak|" + Data._testStopSpot

        var request = Request.Builder()
                .url(url)
                .build()

        Data.client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                //println(e)
            }

            override fun onResponse(call: Call?, response: Response?) {
                var resp = response!!.body()!!.string().toString()
                //resp = Jsoup.parse(resp).text()
                //println("resp" + resp)

                if (resp.length < 2) {
                    //println("ERR")
                } else {
                    var lines: MutableList<String> = resp!!.split("[]").toMutableList()

                    //lines.removeAt(0) // Első sor tartalmazza a mezőneveket

                    for (line in lines) {
                        var items = line.split("|")
                        items = items.map {
                            it.trim()
                        }
                        currBusLines.add(items[1])

                    }
                }
                callback()
            }
        }
        )
    }
}