package com.student.competishun.ui.fragment

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.student.competishun.R
import com.student.competishun.ui.adapter.ViewPagerAdapter
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.appbar.MaterialToolbar

class CoursesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_courses, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabToolbar = view.findViewById<MaterialToolbar>(R.id.appbar)
        val tabViewPager = view.findViewById<ViewPager>(R.id.tab_viewpager)
        val tabTabLayout = view.findViewById<TabLayout>(R.id.tab_tablayout)

        tabToolbar.title = ""
        val clickedViewMessage = arguments?.getString("clicked_view")
        // Use the data as needed
        clickedViewMessage?.let {
            val tittle = view.findViewById<TextView>(R.id.tittle_tb)
            tittle.text = it
            // Log.d("CoursesFragment", "Received message: $it")
        }
        setupViewPager(tabViewPager)
        tabToolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
        tabTabLayout.setupWithViewPager(tabViewPager)
        tabTabLayout.getTabAt(0)?.select()
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = ViewPagerAdapter(childFragmentManager)
        adapter.addFragment(CourseFragment(), "IIT-JEE")
        adapter.addFragment(NEETFragment(), "NEET-UG")
        viewPager.adapter = adapter
    }
}
