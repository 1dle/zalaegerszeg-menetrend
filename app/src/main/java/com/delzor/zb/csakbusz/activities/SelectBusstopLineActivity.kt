package com.delzor.zb.csakbusz.activities

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_select_bus_stop.*
import kotlinx.android.synthetic.main.dialog_search.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.toast
import android.view.inputmethod.InputMethodManager
import com.delzor.zb.csakbusz.BusStop
import com.delzor.zb.csakbusz.Data
import com.delzor.zb.csakbusz.R
import com.delzor.zb.csakbusz.Utils
import com.delzor.zb.csakbusz.adapters.BusStopLineListAdapter


class SelectBusstopLineActivity : AppCompatActivity() {
    var tLines = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_bus_stop)
        setSupportActionBar(findViewById(R.id.toolbarSelectBusStop))

        rvBusStopLineList.layoutManager = LinearLayoutManager(this)
        tLines = intent.getBooleanExtra("LINES",false)
        if(tLines){
            supportActionBar!!.title = "Válaszd ki a járatot!"

            Data.allLineList = mutableListOf()

            Data.fetchBusStopORline(true) {
                runOnUiThread {
                    rvBusStopLineList.adapter = BusStopLineListAdapter(null, Data.allLineList)
                }
            }
        }else{
            supportActionBar!!.title = "Válaszd ki a megállót!"
            Data.busStopList = mutableListOf()
            Data.busStopList.add(BusStop(
                    "2220",
                    "Autóbusz-állomás"
            ))
            Data.busStopList.add(BusStop(
                    "2225",
                    "Vasútállomás"
            ))


            Data.fetchBusStopORline {
                runOnUiThread {
                    rvBusStopLineList.adapter = BusStopLineListAdapter(Data.busStopList)
                }
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(!tLines){
            menuInflater.inflate(R.menu.menu_stop_list, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) =
            when(item.itemId){
                R.id.action_search -> {
                    val view = layoutInflater.inflate(R.layout.dialog_search,null)

                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    val aDialog = alert {
                        title = "Keresés"
                        customView = view
                    }.show()
                    view.btnDialogSearch.onClick{
                        val query = view.etDialogSearch.text.toString()
                        imm.hideSoftInputFromWindow(view.etDialogSearch.windowToken, 0)
                        aDialog.dismiss()

                        val foundedItems = Utils.searchStopByName(query).toMutableList()
                        if(foundedItems.size > 0){
                            runOnUiThread {
                                rvBusStopLineList.adapter = BusStopLineListAdapter(foundedItems)
                            }
                        }else{
                            toast("Nincs találat.")
                        }
                    }

                    true
                }
                else -> super.onOptionsItemSelected(item)
            }

}
