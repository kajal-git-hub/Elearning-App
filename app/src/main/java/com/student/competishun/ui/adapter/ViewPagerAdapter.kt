package com.student.competishun.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class ViewPagerAdapter: FragmentPagerAdapter {

    private var fragmentList1: ArrayList<Fragment> = ArrayList()
    private var fragmentTitleList1: ArrayList<String> = ArrayList()


    public constructor(supportFragmentManager: FragmentManager)
            : super(supportFragmentManager)

    override fun getCount(): Int {
        return fragmentList1.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return fragmentTitleList1[position]
    }
    override fun getItem(position: Int): Fragment {
        return fragmentList1.get(position)
    }

    fun addFragment(fragment: Fragment, title: String) {
        fragmentList1.add(fragment)
        fragmentTitleList1.add(title)
    }

}