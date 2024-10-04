package xyz.penpencil.competishun.ui.fragment

import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import xyz.penpencil.competishun.ui.adapter.ViewPagerAdapter
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.media3.common.util.Log
import com.google.android.material.appbar.MaterialToolbar
import xyz.penpencil.competishun.ui.main.HomeActivity
import xyz.penpencil.competishun.ui.viewmodel.StudentCoursesViewModel
import dagger.hilt.android.AndroidEntryPoint
import xyz.penpencil.competishun.R

@AndroidEntryPoint
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


        (activity as? HomeActivity)?.showBottomNavigationView(false)
        (activity as? HomeActivity)?.showFloatingButton(true)

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

        clickedViewMessage?.let {
            val tittle = view.findViewById<TextView>(R.id.tittle_tb)
            Log.e("CoursesFragment", "Received message: $it")
            tittle.text = it

        }
        setupViewPager(tabViewPager,examIIT,examNEET)
        tabToolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
        tabTabLayout.setupWithViewPager(tabViewPager)
        tabTabLayout.getTabAt(0)?.select()
    }

    private fun setupViewPager(viewPager: ViewPager, examIIT: String, examNEET: String) {
        val adapter = ViewPagerAdapter(childFragmentManager)

        val bundleIIT = Bundle().apply {
            putString("category_name", categoryName)
            putString("exam_type", examIIT)  // Passing IIT-JEE as exam type
        }
        val courseFragment = CourseFragment().apply { arguments = bundleIIT }

        val bundleNEET = Bundle().apply {
            putString("category_name", categoryName)
            putString("exam_type", examNEET)  // Passing NEET as exam type
        }
        val neetFragment = NEETFragment().apply { arguments = bundleNEET }

        adapter.addFragment(courseFragment, examIIT)
        adapter.addFragment(neetFragment, examNEET)
        viewPager.adapter = adapter
    }

}