package com.delzor.zb.csakbusz.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.delzor.zb.csakbusz.*
import kotlinx.android.synthetic.main.activity_main_menu.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick

class MainMenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)
        setSupportActionBar(toolbarMenu)


        btnMenuNearStops.onClick {
            startActivity<BusStopsLocationMapActivity>(
                    "NEAR" to true
            )
        }
        btnMenuAllStops.onClick {
            startActivity<SelectBusstopLineActivity>()
        }
        btnMenuOnlineBus.onClick {
            startActivity<SelectBusActivity>()
        }
        btnMenuLines.onClick {
            startActivity<SelectBusstopLineActivity>(
                    "LINES" to true
            )
        }

        if (Utils.isOnline(this)) {
            showMenu(true)
        } else {
            showMenu(false)
            tvMenuMSG.text = "Az alkalmazás működéséhez állandó internetkapcsolatra van szükség, kérlek kapcsold be, ha szeretnéd használni az alkalmazást"
            tvMenuMSG.visibility = View.VISIBLE
            btnReloadMenu.visibility = View.VISIBLE
        }
        btnReloadMenu.onClick {
            if (Utils.isOnline(act)) {
                showMenu(true)
                tvMenuMSG.visibility = View.GONE
                btnReloadMenu.visibility = View.GONE
            }else{
                tvMenuMSG.text = "Még mindig nincs net :/"
            }
        }
    }
    fun showMenu(show: Boolean){
        if(show){
            btnMenuLines.visibility = View.VISIBLE
            btnMenuOnlineBus.visibility = View.VISIBLE
            btnMenuAllStops.visibility = View.VISIBLE
            btnMenuNearStops.visibility = View.VISIBLE
        }else{
            btnMenuLines.visibility = View.GONE
            btnMenuOnlineBus.visibility = View.GONE
            btnMenuAllStops.visibility = View.GONE
            btnMenuNearStops.visibility = View.GONE
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.action_info) {

            alert{
                title = "Zalaegerszeg menetrend app"
                message = Utils.setTextHTML(Data.infohtml)
                negativeButton("Bezárás") {}
                positiveButton("Visszajelzés") {
                    browse("https://idkfa.hu/feedback.php?app=zme")
                }
            }.show()

            return true
        }

        return super.onOptionsItemSelected(item)
    }
}
