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
import androidx.fragment.app.viewModels
import androidx.media3.common.util.Log
import com.google.android.material.appbar.MaterialToolbar
import com.student.competishun.curator.type.FindAllCourseInputStudent
import com.student.competishun.ui.viewmodel.StudentCoursesViewModel

class CoursesFragment : Fragment() {
    private val  studentCoursesViewModel: StudentCoursesViewModel by viewModels()
    var categoryName = ""
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


        val clickedViewMessage = arguments?.getString("clicked_view")
        categoryName = arguments?.getString("course_name").toString()
        val categoryId = arguments?.getString("category_id")
        tabToolbar.title = ""
        tabToolbar.title = categoryName
        Log.e("getcaourws $categoryId",categoryName.toString())
        val examIIT = "IIT-JEE"
        val examNEET = "NEET"
        // Use the data as needed
        clickedViewMessage?.let {
            val tittle = view.findViewById<TextView>(R.id.tittle_tb)
            tittle.text = it
            // Log.d("CoursesFragment", "Received message: $it")
        }
        setupViewPager(tabViewPager,examIIT,examNEET)
        tabToolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
        tabTabLayout.setupWithViewPager(tabViewPager)
        tabTabLayout.getTabAt(0)?.select()
    }

    private fun setupViewPager(viewPager: ViewPager,examIIT: String, examNEET: String) {
        val adapter = ViewPagerAdapter(childFragmentManager)
        val bundle = Bundle().apply {
            putString("category_name", categoryName)  // Include other data if needed
        }
        val courseFragment = CourseFragment().apply { arguments = bundle }
        val neetFragment = NEETFragment().apply { arguments = bundle }

        adapter.addFragment(courseFragment, examIIT)
        adapter.addFragment(neetFragment, examNEET)
        viewPager.adapter = adapter
    }
}
