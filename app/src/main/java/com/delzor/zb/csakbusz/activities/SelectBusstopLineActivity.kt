package com.delzor.zb.csakbusz.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast
import android.view.inputmethod.InputMethodManager
import com.delzor.zb.csakbusz.BusStop
import com.delzor.zb.csakbusz.Data
import com.delzor.zb.csakbusz.R
import com.delzor.zb.csakbusz.Utils
import com.delzor.zb.csakbusz.adapters.BusStopLineListAdapter
import com.delzor.zb.csakbusz.databinding.ActivitySelectBusStopBinding
import com.delzor.zb.csakbusz.databinding.DialogSearchBinding


class SelectBusstopLineActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelectBusStopBinding
    private lateinit var dialogBinding: DialogSearchBinding

    var tLines = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectBusStopBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarSelectBusStop)

        //dialogbox layout
        dialogBinding = DialogSearchBinding.inflate(layoutInflater)

        binding.rvBusStopLineList.layoutManager = LinearLayoutManager(this)
        tLines = intent.getBooleanExtra("LINES",false)
        if(tLines){
            supportActionBar!!.title = "Válaszd ki a járatot!"

            Data.allLineList = mutableListOf()

            Data.fetchBusStopORline(true) {
                runOnUiThread {
                    binding.rvBusStopLineList.adapter = BusStopLineListAdapter(null, Data.allLineList)
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
                    binding.rvBusStopLineList.adapter = BusStopLineListAdapter(Data.busStopList)
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
                    val view = dialogBinding

                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    val aDialog = alert {
                        title = "Keresés"
                        customView = view.root
                    }.show()
                    view.btnDialogSearch.setOnClickListener{
                        val query = view.etDialogSearch.text.toString()
                        imm.hideSoftInputFromWindow(view.etDialogSearch.windowToken, 0)
                        aDialog.dismiss()

                        val foundedItems = Utils.searchStopByName(query).toMutableList()
                        if(foundedItems.size > 0){
                            runOnUiThread {
                                binding.rvBusStopLineList.adapter = BusStopLineListAdapter(foundedItems)
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
