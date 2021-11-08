package com.letyouknow.view.home.dealsummary.gallery360view

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.letyouknow.view.home.dealsummary.gallery360view.gallery.ExteriorFragment
import com.letyouknow.view.home.dealsummary.gallery360view.view360.View360Fragment

class Gallery360PagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    override fun getCount(): Int = 2
    private var arTitle = arrayListOf("IMAGES", "360 VIEW")

    override fun getPageTitle(position: Int): CharSequence {
        return arTitle[position]
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                ExteriorFragment()
            }
            /* 1 -> {
                 ExteriorFragment()
             }*/
            else -> {
                View360Fragment()
            }
        }
    }

}