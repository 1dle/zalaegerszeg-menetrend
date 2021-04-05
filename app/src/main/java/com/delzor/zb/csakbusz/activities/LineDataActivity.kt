package com.delzor.zb.csakbusz.activities

import com.google.android.material.tabs.TabLayout
import androidx.appcompat.app.AppCompatActivity

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import android.os.Bundle
import android.widget.Toolbar
import com.delzor.zb.csakbusz.Data
import com.delzor.zb.csakbusz.R
import com.delzor.zb.csakbusz.Subline
import com.delzor.zb.csakbusz.databinding.ActivityLineDataBinding
import com.delzor.zb.csakbusz.fragments.LineListFragment
import com.delzor.zb.csakbusz.fragments.PathListFragment
import com.delzor.zb.csakbusz.fragments.PathMapFragment

class LineDataActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLineDataBinding
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLineDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.title = intent.extras!!.getString("TITLE","Válassz a listából!").replace("- ","")

        setSupportActionBar(binding.toolbar)
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        binding.container.offscreenPageLimit = 2
        binding.container.adapter = mSectionsPagerAdapter

        binding.container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(binding.tabs))
        binding.tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(binding.container))

    }

    override fun onDestroy() {
        Data.selectedSubLine = Subline()
        super.onDestroy()
    }

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
