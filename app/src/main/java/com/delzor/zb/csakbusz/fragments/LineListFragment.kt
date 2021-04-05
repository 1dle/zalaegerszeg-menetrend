package com.delzor.zb.csakbusz.fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.viewpager.widget.ViewPager
import com.delzor.zb.csakbusz.Data
import com.delzor.zb.csakbusz.Data.fetchLineData
import com.delzor.zb.csakbusz.Line
import com.delzor.zb.csakbusz.adapters.LineListAdapter
import com.delzor.zb.csakbusz.R
import com.delzor.zb.csakbusz.databinding.FragmentLinesListBinding
import org.jetbrains.anko.alert
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.yesButton
import java.io.IOException

class LineListFragment : Fragment() {

    private var _binding: FragmentLinesListBinding? = null
    private val binding get() = _binding!!

    var currLines = mutableListOf<Line>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLinesListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchLineByNum(Data._testLineNum) {
            when (it) {
                Data.RESP.NODATA -> activity!!.runOnUiThread{activity!!.alert {
                    title = "Nincs adat!"
                    message = "A mai napon ez a járat nem indul"
                    yesButton { activity!!.finish() }
                    onCancelled { activity!!.finish() }

                }.show()}
                Data.RESP.SUCCESSFUL -> activity!!!!.runOnUiThread {
                    binding.rvSublineList.layoutManager = LinearLayoutManager(context)
                    binding.rvSublineList.adapter = LineListAdapter(currLines) {
                        // onclick
                        var dlDialog = activity!!.indeterminateProgressDialog("Adatok letöltése")
                        dlDialog.setCancelable(false)
                        dlDialog.show()
                        val name = it.name
                        fetchLineData(it.id) {
                            if (!it.any {
                                        it.split("_")[0] == "ERR"
                                    }) {
                                activity!!.runOnUiThread {
                                    dlDialog.hide()
                                    dlDialog.dismiss()
                                    activity!!.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar).title = name
                                    //println("Sikeres letöltés")
                                    activity!!.findViewById<ViewPager>(R.id.container).setCurrentItem(1, true)
                                }
                            } else {
                                activity!!.finish()
                            }

                        }
                    }
                }
            }
        }
    }

    fun fetchLineByNum(num: String, callback: (Data.RESP) -> Unit) {
        val url = "http://zalaegerszeg.enykk.hu/php/sql.php?id=mod_vonal2|$num|O|undefined"

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
                    callback(Data.RESP.NODATA)
                } else {
                    var lines: MutableList<String> = resp!!.split("[]").toMutableList()

                    //lines.removeAt(0) // Első sor tartalmazza a mezőneveket

                    for (line in lines) {
                        var items = line.split("|")
                        items.forEach { it.trim() }
                        var curr = Line(
                                items[0].toInt(),
                                items[1],
                                items[2]
                        )
                        currLines.add(curr)
                    }
                    callback(Data.RESP.SUCCESSFUL)

                }
            }
        }
        )
    }

}
