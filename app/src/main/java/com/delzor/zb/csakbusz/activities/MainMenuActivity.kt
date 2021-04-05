package com.delzor.zb.csakbusz.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.delzor.zb.csakbusz.*
import com.delzor.zb.csakbusz.databinding.ActivityMainMenuBinding
import org.jetbrains.anko.alert
import org.jetbrains.anko.browse


class MainMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarMenu)

        binding.btnMenuNearStops.setOnClickListener {
            Intent(this, BusStopsLocationMapActivity::class.java).apply {
                putExtra("NEAR",true)
            }.also {
                startActivity(it)
            }
        }

        binding.btnMenuAllStops.setOnClickListener {
            Intent(this, SelectBusstopLineActivity::class.java).also{
                startActivity(it)
            }
        }
        binding.btnMenuOnlineBus.setOnClickListener {
            Intent(this, SelectBusActivity::class.java).also{
                startActivity(it)
            }
        }
        binding.btnMenuLines.setOnClickListener {

            Intent(this, SelectBusstopLineActivity::class.java).apply {
                putExtra("LINES",true)
            }.also {
                startActivity(it)
            }
        }

        if (Utils.isOnline(this)) {
            showMenu(true)
        } else {
            showMenu(false)
            binding.tvMenuMSG.text = "Az alkalmazás működéséhez állandó internetkapcsolatra van szükség, kérlek kapcsold be, ha szeretnéd használni az alkalmazást"
            binding.tvMenuMSG.visibility = View.VISIBLE
            binding.btnReloadMenu.visibility = View.VISIBLE
        }
        binding.btnReloadMenu.setOnClickListener {
            if (Utils.isOnline(this)) {
                showMenu(true)
                binding.tvMenuMSG.visibility = View.GONE
                binding.btnReloadMenu.visibility = View.GONE
            }else{
                binding.tvMenuMSG.text = "Még mindig nincs net :/"
            }
        }
    }
    fun showMenu(show: Boolean){
        if(show){
            binding.btnMenuLines.visibility = View.VISIBLE
            binding.btnMenuOnlineBus.visibility = View.VISIBLE
            binding.btnMenuAllStops.visibility = View.VISIBLE
            binding.btnMenuNearStops.visibility = View.VISIBLE
        }else{
            binding.btnMenuLines.visibility = View.GONE
            binding.btnMenuOnlineBus.visibility = View.GONE
            binding.btnMenuAllStops.visibility = View.GONE
            binding.btnMenuNearStops.visibility = View.GONE
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
