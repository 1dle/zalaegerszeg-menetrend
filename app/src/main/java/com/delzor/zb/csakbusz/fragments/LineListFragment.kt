package com.delzor.zb.csakbusz.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.delzor.zb.csakbusz.Data
import com.delzor.zb.csakbusz.Data.fetchLineData
import com.delzor.zb.csakbusz.Line
import com.delzor.zb.csakbusz.adapters.LineListAdapter
import com.delzor.zb.csakbusz.R
import kotlinx.android.synthetic.main.activity_line_data.*
import kotlinx.android.synthetic.main.fragment_lines_list.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import org.jetbrains.anko.support.v4.act
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.indeterminateProgressDialog
import org.jetbrains.anko.yesButton
import java.io.IOException

class LineListFragment : Fragment() {

    var currLines = mutableListOf<Line>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = inflater.inflate(R.layout.fragment_lines_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchLineByNum(Data._testLineNum) {
            when (it) {
                Data.RESP.NODATA -> act.runOnUiThread{alert {
                    title = "Nincs adat!"
                    message = "A mai napon ez a járat nem indul"
                    yesButton { act.finish() }
                    onCancelled { act.finish() }

                }.show()}
                Data.RESP.SUCCESSFUL -> act!!.runOnUiThread {
                    rvSublineList.layoutManager = LinearLayoutManager(context)
                    rvSublineList.adapter = LineListAdapter(currLines) {
                        // onclick
                        var dlDialog = indeterminateProgressDialog("Adatok letöltése")
                        dlDialog.setCancelable(false)
                        dlDialog.show()
                        val name = it.name
                        fetchLineData(it.id) {
                            if (!it.any {
                                        it.split("_")[0] == "ERR"
                                    }) {
                                act!!.runOnUiThread {
                                    dlDialog.hide()
                                    dlDialog.dismiss()
                                    act!!.toolbar.title = name
                                    //println("Sikeres letöltés")
                                    act!!.container.setCurrentItem(1, true)
                                }
                            } else {
                                act!!.finish()
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
