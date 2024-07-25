package com.student.competishun.ui.fragment.explorefragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.core.widget.NestedScrollView
import com.google.android.material.tabs.TabLayout
import com.student.competishun.R

class ExploreFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_explore, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabLayout = view.findViewById<TabLayout>(R.id.tab_tablayout)
        val nestedScrollView = view.findViewById<NestedScrollView>(R.id.nested_scroll_view)
        val clOurContent = view.findViewById<View>(R.id.clOurContent)
        val clCourseFeatures = view.findViewById<View>(R.id.clCourseFeatures)
        val clCoursePlanner = view.findViewById<View>(R.id.clCoursePlanner)
        val clCourseTeachers = view.findViewById<View>(R.id.clTeachers)

        tabLayout.addTab(tabLayout.newTab().setText("Content"))
        tabLayout.addTab(tabLayout.newTab().setText("Features"))
        tabLayout.addTab(tabLayout.newTab().setText("Planner"))
        tabLayout.addTab(tabLayout.newTab().setText("Teachers"))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> nestedScrollView.scrollTo(0, clOurContent.top)
                    1 -> nestedScrollView.scrollTo(0, clCourseFeatures.top)
                    2 -> nestedScrollView.scrollTo(0, clCoursePlanner.top)
                    3 -> nestedScrollView.scrollTo(0, clCourseTeachers.top)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}

            override fun onTabReselected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> nestedScrollView.scrollTo(0, clOurContent.top)
                    1 -> nestedScrollView.scrollTo(0, clCourseFeatures.top)
                    2 -> nestedScrollView.scrollTo(0, clCoursePlanner.top)
                    3 -> nestedScrollView.scrollTo(0, clCourseTeachers.top)
                }
            }
        })
    }
}
