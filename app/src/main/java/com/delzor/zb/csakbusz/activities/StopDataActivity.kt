package com.delzor.zb.csakbusz.activities

import com.google.android.material.tabs.TabLayout
import androidx.appcompat.app.AppCompatActivity

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import android.os.Bundle
import com.delzor.zb.csakbusz.Data._testStopSpot
import com.delzor.zb.csakbusz.Data.numOfDownloadedPages
import com.delzor.zb.csakbusz.Data.timer
import com.delzor.zb.csakbusz.fragments.*

import org.jetbrains.anko.indeterminateProgressDialog
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate
import com.delzor.zb.csakbusz.Data
import com.delzor.zb.csakbusz.Data.currStopSpots
import com.delzor.zb.csakbusz.Data.nearStopSpots
import com.delzor.zb.csakbusz.R
import com.delzor.zb.csakbusz.Utils
import com.delzor.zb.csakbusz.databinding.ActivityStopDataBinding


class StopDataActivity : AppCompatActivity() {

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    private lateinit var binding: ActivityStopDataBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStopDataBinding.inflate(layoutInflater)

        setContentView(binding.root)

        _testStopSpot = intent.extras!!.get("STOPSPOT_ID").toString()
        var near = intent.extras!!.getBoolean("NEAR",false)



        binding.toolbar.title = (if(near) Utils.nearStopSpotNameByID(_testStopSpot) else Data._testStopSpotName) + " - "+ (Utils.stopSpotNObyID(_testStopSpot, if (near) nearStopSpots else currStopSpots))

        setSupportActionBar(binding.toolbar)
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        binding.container.adapter = mSectionsPagerAdapter
        binding.container.offscreenPageLimit = 2

        binding.container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(binding.tabs))
        binding.tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(binding.container))

        numOfDownloadedPages = 0
        val dialog = indeterminateProgressDialog("Adatok letöltése")
        dialog.setCancelable(false)
        dialog.show()
        timer = Timer()
        timer.scheduleAtFixedRate(0,2500){
            if(numOfDownloadedPages == 3){
                runOnUiThread {
                    dialog.hide()
                    numOfDownloadedPages = 0
                    dialog.dismiss()
                    timer.cancel()
                }
            }
        }

    }

    override fun onPause() {
        super.onPause()
        timer.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }

    override fun onStop() {
        super.onStop()
        timer.cancel()
    }
/*
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_stop_data, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }*/
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            return when(position){
                0 -> ArrivalsFragment()
                1 -> TimetableFragment()
                else -> BuslinesFragment()
            }
        }

        override fun getCount(): Int {
            // Show 3 total pages.
            return 3
        }
    }
}
