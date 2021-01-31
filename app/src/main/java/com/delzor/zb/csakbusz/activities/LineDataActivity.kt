package com.delzor.zb.csakbusz.activities

import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.os.Bundle
import com.delzor.zb.csakbusz.Data
import com.delzor.zb.csakbusz.R
import com.delzor.zb.csakbusz.Subline
import com.delzor.zb.csakbusz.fragments.LineListFragment
import com.delzor.zb.csakbusz.fragments.PathListFragment
import com.delzor.zb.csakbusz.fragments.PathMapFragment

import kotlinx.android.synthetic.main.activity_line_data.*

class LineDataActivity : AppCompatActivity() {


    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_line_data)

        toolbar.title = intent.extras.getString("TITLE","Válassz a listából!").replace("- ","")

        setSupportActionBar(toolbar)
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        container.offscreenPageLimit = 2
        container.adapter = mSectionsPagerAdapter

        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))

    }

    override fun onDestroy() {
        Data.selectedSubLine = Subline()
        super.onDestroy()
    }

    /*
        override fun onCreateOptionsMenu(menu: Menu): Boolean {
            // Inflate the menu; this adds items to the action bar if it is present.
            menuInflater.inflate(R.menu.menu_line_data, menu)
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
        }
        */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> LineListFragment()
                1 -> PathMapFragment()
                else -> PathListFragment()
            }
        }

        override fun getCount(): Int {
            // Show 3 total pages.
            return 3
        }
    }
}
