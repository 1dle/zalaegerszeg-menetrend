package com.delzor.zb.csakbusz.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.delzor.zb.csakbusz.Data
import com.delzor.zb.csakbusz.Data.fetchOnlineBus
import com.delzor.zb.csakbusz.R
import com.delzor.zb.csakbusz.adapters.BuslistAdapter
import com.delzor.zb.csakbusz.databinding.ActivitySelectBusBinding
import org.jetbrains.anko.alert
import org.jetbrains.anko.yesButton

class SelectBusActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelectBusBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectBusBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.swiperefreshList.isRefreshing = true

        binding.rvBusList.layoutManager = GridLayoutManager(this, 3)

        binding.swiperefreshList.setOnRefreshListener {
            Data.onlinebusList = mutableListOf()
            binding.rvBusList.adapter = null
            binding.swiperefreshList.isRefreshing = true
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
                        binding.rvBusList.adapter = BuslistAdapter(Data.onlinebusList)
                        binding.swiperefreshList.isRefreshing = false
                    }

                }

            }

        }

    }

    override fun onResume() {

        binding.rvBusList.adapter = null
        Data.onlinebusList = mutableListOf()
        binding.swiperefreshList.isRefreshing = true
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
                    binding.rvBusList.adapter = BuslistAdapter(Data.onlinebusList)
                    binding.swiperefreshList.isRefreshing = false
                }

            }

        }

        super.onResume()
    }
}

