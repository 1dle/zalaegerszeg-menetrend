package com.delzor.zb.csakbusz.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.delzor.zb.csakbusz.*
import com.delzor.zb.csakbusz.adapters.ArrivalsListAdapter
import com.delzor.zb.csakbusz.databinding.FragmentArrivalsBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class ArrivalsFragment: Fragment(){

    private var _binding: FragmentArrivalsBinding? = null
    private val binding get() = _binding!!

    var currArrivals = mutableListOf<Arrival>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        _binding = FragmentArrivalsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        fetchStopData {
            when(it){
                Data.RESP.SUCCESSFUL -> activity!!.runOnUiThread{
                    binding.rvArrivals1.layoutManager = LinearLayoutManager(context)
                    binding.rvArrivals1.adapter = ArrivalsListAdapter(currArrivals)
                    binding.tvArrivalsMsg.visibility = View.GONE
                }
                Data.RESP.NODATA -> activity!!.runOnUiThread{
                    binding.tvArrivalsMsg.text = "Nem érkezik busz a következő órában."
                    binding.tvArrivalsMsg.visibility = View.VISIBLE
                }
                Data.RESP.ERROR -> activity!!.runOnUiThread{
                    binding.tvArrivalsMsg.text = "Hiba. Nem sikerült csatlakozni a szerverhez"
                    binding.tvArrivalsMsg.visibility = View.VISIBLE
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

        val request = Request.Builder()
                .url(url)
                .build()

        Data.client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                callback(Data.RESP.ERROR)
            }

            override fun onResponse(call: Call?, response: Response?) {
                val resp = response!!.body()!!.string().toString()
                //resp = Jsoup.parse(resp).text()
                if (resp.length < 2){
                    callback(Data.RESP.NODATA)
                }else{
                    val lines: MutableList<String> = resp.split("\n").toMutableList()

                    lines.removeAt(0) // Első sor tartalmazza a mezőneveket

                    for(line in lines){
                        val items = line.split("|")

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