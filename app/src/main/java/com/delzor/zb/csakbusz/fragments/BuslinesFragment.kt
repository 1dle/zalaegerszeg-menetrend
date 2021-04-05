package com.delzor.zb.csakbusz.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.delzor.zb.csakbusz.*
import com.delzor.zb.csakbusz.adapters.SimpleListAdapter
import com.delzor.zb.csakbusz.databinding.FragmentBuslinesBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import org.jetbrains.anko.support.v4.act
import java.io.IOException

class BuslinesFragment : Fragment() {

    private var _binding: FragmentBuslinesBinding? = null
    private val binding get() = _binding!!

    var currBusLines = mutableListOf<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentBuslinesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        fetchBusLinesFData{
            activity!!.runOnUiThread {
                binding.rvBuslinesFragment.layoutManager = LinearLayoutManager(context)
                binding.rvBuslinesFragment.adapter = SimpleListAdapter(currBusLines, 16f, true)
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