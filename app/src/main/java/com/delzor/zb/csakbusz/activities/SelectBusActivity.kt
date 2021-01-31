package com.delzor.zb.csakbusz.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import com.delzor.zb.csakbusz.Data
import com.delzor.zb.csakbusz.Data.fetchOnlineBus
import com.delzor.zb.csakbusz.R
import com.delzor.zb.csakbusz.adapters.BuslistAdapter
import kotlinx.android.synthetic.main.activity_select_bus.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.yesButton

class SelectBusActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_bus)

        swiperefreshList.isRefreshing = true

        rvBusList.layoutManager = GridLayoutManager(this, 3)

        swiperefreshList.setOnRefreshListener {
            Data.onlinebusList = mutableListOf()
            rvBusList.adapter = null
            swiperefreshList.isRefreshing = true
            fetchOnlineBus {
                when(it){
                    Data.RESP.NODATA -> {alert {
                            title = "Figyelem!"
                            message = "Nincs elérhető online busz"
                            onCancelled { finish() }
                            yesButton { finish() }

                        }.show()
                    }
                    Data.RESP.ERROR -> {alert {
                            title = "Hiba!"
                            message = "Sikertelen kommunikáció a szerverrel"
                            onCancelled { finish() }
                            yesButton { finish() }

                        }.show()
                    }
                    Data.RESP.SUCCESSFUL -> runOnUiThread {
                        rvBusList.adapter = BuslistAdapter(Data.onlinebusList)
                        swiperefreshList.isRefreshing = false
                    }

                }

            }

        }

    }

    override fun onResume() {

        rvBusList.adapter = null
        Data.onlinebusList = mutableListOf()
        swiperefreshList.isRefreshing = true
        fetchOnlineBus {
            when(it){
                Data.RESP.NODATA -> {alert {
                    title = "Figyelem!"
                    message = "Nincs elérhető online busz"
                    onCancelled { finish() }
                    yesButton { finish() }

                }
                }
                Data.RESP.ERROR -> {alert {
                    title = "Hiba!"
                    message = "Sikertelen kommunikáció a szerverrel"
                    onCancelled { finish() }
                    yesButton { finish() }

                }
                }
                Data.RESP.SUCCESSFUL -> runOnUiThread {
                    rvBusList.adapter = BuslistAdapter(Data.onlinebusList)
                    swiperefreshList.isRefreshing = false
                }

            }

        }

        super.onResume()
    }
}

