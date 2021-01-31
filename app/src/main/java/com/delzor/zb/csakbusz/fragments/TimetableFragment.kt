package com.delzor.zb.csakbusz.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.delzor.zb.csakbusz.*
import com.delzor.zb.csakbusz.adapters.TimeTableListAdapter
import kotlinx.android.synthetic.main.fragment_timetable.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import org.jetbrains.anko.sdk25.coroutines.onClick
import java.io.IOException
import java.util.*

class TimetableFragment: Fragment(){

    var currTimeTable = mutableListOf<TimeTable>()
    val c = Calendar.getInstance()
    val year = c.get(Calendar.YEAR)
    val month = c.get(Calendar.MONTH)
    val day = c.get(Calendar.DAY_OF_MONTH)
    val cnt = context

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_timetable, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val today = Utils.dateFormat(year,month,day)
        tvDatePicked.text = today
        fetchTimeTableData(today) {
            when(it){
                Data.RESP.NODATA -> activity!!.runOnUiThread {
                    tvTimeTableMSG.text = "Ezen a napon nem érkezik busz a megállóba"
                    tvTimeTableMSG.visibility = View.VISIBLE
                    rvTimeTable.adapter = null
                }
                Data.RESP.SUCCESSFUL -> activity!!.runOnUiThread {
                    tvTimeTableMSG.visibility = View.GONE
                    rvTimeTable.layoutManager = LinearLayoutManager(cnt)
                    rvTimeTable.adapter = TimeTableListAdapter(currTimeTable)

                }
            }
            Data.numOfDownloadedPages++
        }

        btnDatePicker.onClick{

            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val picked = Utils.dateFormat(year,monthOfYear,dayOfMonth)
                tvDatePicked.text = picked
                currTimeTable = mutableListOf()
                fetchTimeTableData(picked) {
                    when(it){
                        Data.RESP.NODATA -> activity!!.runOnUiThread {
                            tvTimeTableMSG.text = "Ezen a napon nem érkezik busz a megállóba"
                            tvTimeTableMSG.visibility = View.VISIBLE
                            rvTimeTable.adapter = null
                        }
                        Data.RESP.SUCCESSFUL -> activity!!.runOnUiThread {
                            tvTimeTableMSG.visibility = View.GONE
                            rvTimeTable.layoutManager = LinearLayoutManager(cnt)
                            rvTimeTable.adapter = TimeTableListAdapter(currTimeTable)

                        }
                    }

                }

            }, year, month, day)
            dpd.show()
        }

    }
    fun fetchTimeTableData(date: String,callback:(Data.RESP) -> Unit){

        val url = "http://zalaegerszeg.enykk.hu/php/sql.php?id=kdsm|"+ Data._testStopSpot + "|"+date

        var request = Request.Builder()
                .url(url)
                .build()

        Data.client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                //println(e)
            }

            override fun onResponse(call: Call?, response: Response?) {
                var resp = response!!.body()!!.string().toString()
                //resp = Jsoup.parse(resp).text()

                if (resp.length < 2){
                    callback(Data.RESP.NODATA)
                }else{
                    var lines: MutableList<String> = resp!!.split("[]").toMutableList()

                    //lines.removeAt(0) // Első sor tartalmazza a mezőneveket
                    var new = true
                    var prev = arrayOf("0","0","0")
                    var times = mutableListOf<String>()

                    for(line in lines){
                        var items = line.split("|")
                        items = items.map {
                            it.trim()
                        }
                        if(lines.last() == line){
                            if (prev[0] == items[1]){
                                if(!times.contains(items[2])){
                                    times.add(items[2])
                                }
                                currTimeTable.add(TimeTable(
                                        times,
                                        prev[1],
                                        prev[2]
                                ))
                            }else{
                                //utolsó elötti hozzáadása
                                currTimeTable.add(TimeTable(
                                        times,
                                        prev[1],
                                        prev[2]
                                ))
                                times = mutableListOf()
                                times.add(items[2])
                                //utolsó hozzáadása
                                currTimeTable.add(TimeTable(
                                        times,
                                        items[3],
                                        items[4]
                                ))
                            }
                            break
                        }else{
                            if(new || prev[0] == items[1]){
                                if(!times.contains(items[2])){
                                    times.add(items[2])
                                }

                                prev = arrayOf(items[1],items[3],items[4]) //járatnév és végállomás
                                new = false
                            }else{
                                currTimeTable.add(TimeTable(
                                        times,
                                        prev[1],
                                        prev[2]
                                ))
                                times = mutableListOf()
                                times.add(items[2])

                                prev = arrayOf(items[1],items[3],items[4]) //járatnév és végállomás
                            }
                        }

                    }
                    callback(Data.RESP.SUCCESSFUL)
                }

            }
        }
        )

    }
}