package com.student.competishun.ui.fragment

import RecommendedCoursesAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo3.api.Optional
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.student.competishun.R
import com.student.competishun.curator.GetAllCourseCategoriesQuery
import com.student.competishun.curator.GetAllCourseQuery
import com.student.competishun.curator.type.CourseStatus
import com.student.competishun.curator.type.FindAllCourseInput
import com.student.competishun.data.model.PromoBannerModel
import com.student.competishun.data.model.RecommendedCourseDataModel
import com.student.competishun.data.model.Testimonial
import com.student.competishun.data.model.WhyCompetishun
import com.student.competishun.databinding.FragmentHomeBinding
import com.student.competishun.ui.adapter.OurCoursesAdapter
import com.student.competishun.ui.adapter.PromoBannerAdapter
import com.student.competishun.ui.adapter.TestimonialsAdapter
import com.student.competishun.ui.adapter.WhyCompetishunAdapter
import com.student.competishun.ui.viewmodel.CoursesCategoryViewModel
import com.student.competishun.ui.viewmodel.CoursesViewModel
import com.student.competishun.utils.Constants
import com.student.competishun.utils.HelperFunctions
import com.student.competishun.utils.OnCourseItemClickListener
import dagger.hilt.android.AndroidEntryPoint
import java.io.Serializable

@AndroidEntryPoint
class HomeFragment : Fragment(), OnCourseItemClickListener {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var rvWhyCompetishun: RecyclerView
    private lateinit var dotsIndicatorTestimonials: LinearLayout
    private lateinit var dotsIndicatorWhyCompetishun: LinearLayout
    private lateinit var adapter: TestimonialsAdapter
    private lateinit var adapterWhyCompetishun: WhyCompetishunAdapter
    private lateinit var testimonials: List<Testimonial>
    private lateinit var listWhyCompetishun: List<WhyCompetishun>
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var toggle: ActionBarDrawerToggle
    private val coursesViewModel: CoursesViewModel by viewModels()
    private val coursesCategoryViewModel: CoursesCategoryViewModel by viewModels()
    private lateinit var rvOurCourses: RecyclerView
    private lateinit var dotsIndicatorOurCourses: LinearLayout
    private lateinit var adapterOurCourses: OurCoursesAdapter
    private var listOurCoursesItem: List<GetAllCourseCategoriesQuery.GetAllCourseCategory>? = null

    private lateinit var promoBannerList: List<PromoBannerModel>

    private lateinit var recommendedCourseList: List<RecommendedCourseDataModel>

    private lateinit var helperFunctions: HelperFunctions

    private lateinit var contactImage: ImageView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false).apply {
            this.coursesViewModel = this@HomeFragment.coursesViewModel
            this.coursesCategoryViewModel = this@HomeFragment.coursesCategoryViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        rvOurCourses = view.findViewById(R.id.rvOurCourses)
        dotsIndicatorOurCourses = view.findViewById(R.id.llDotsIndicatorOurCourses)
        rvOurCourses.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                updateDotsIndicator(recyclerView, dotsIndicatorOurCourses, 6)
            }
        })


        helperFunctions = HelperFunctions()

        drawerLayout = view.findViewById(R.id.drwaer_layout)
        bottomNav = requireActivity().findViewById(R.id.bottomNav)
        val toolbar: MaterialToolbar = view.findViewById(R.id.topAppBar)
        contactImage = requireActivity().findViewById(R.id.ig_ContactImage)

        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_notification -> {
                    findNavController().navigate(R.id.action_homeFragment_to_NotificationFragment)
                    true
                }

                R.id.action_search -> {
                    true
                }

                R.id.action_profile -> {
                    findNavController().navigate(R.id.action_homeFragment_to_ProfileFragment)
                    true
                }

                else -> false
            }
        }

        toggle = ActionBarDrawerToggle(
            activity, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()


        drawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerOpened(drawerView: View) {
                bottomNav.visibility = View.GONE
                contactImage.visibility = View.GONE
            }

            override fun onDrawerClosed(drawerView: View) {
                bottomNav.visibility = View.VISIBLE
                contactImage.visibility = View.VISIBLE
            }
        })

        recyclerView = view.findViewById(R.id.recyclerViewTestimonials)
        rvWhyCompetishun = view.findViewById(R.id.rvWhyCompetishun)
        dotsIndicatorTestimonials = view.findViewById(R.id.llDotsIndicator)
        dotsIndicatorWhyCompetishun = view.findViewById(R.id.llDotsIndicatorWhyCompetishun)

        coursesViewModel.courses.observe(viewLifecycleOwner, Observer { courses ->
            Log.e("Coursesres", courses?.get(7)?.banner_image.toString())
            Log.e("Coursesres", courses?.get(8)?.banner_image.toString())
            Log.e("Coursesres", courses?.get(9)?.banner_image.toString())

            val bannerList = mutableListOf<PromoBannerModel>()

            courses?.forEach { course ->
                Log.e("Course", course.toString())
                bannerList.add(PromoBannerModel(course.banner_image))
            }


            binding.rvpromobanner.adapter = PromoBannerAdapter(bannerList)
            binding.rvpromobanner.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            setupDotsIndicator(bannerList.size, binding.llDotsIndicatorPromoBanner)
            binding.rvpromobanner.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    updateDotsIndicator(recyclerView, binding.llDotsIndicatorPromoBanner)
                }
            })

            binding.rvRecommendedCourses.adapter = courses?.let { courseList ->
                RecommendedCoursesAdapter(courseList) { selectedCourse ->
                    val bundle = Bundle().apply {
                        putString("course_id", selectedCourse.id)
                    }
                    findNavController().navigate(R.id.exploreFragment,bundle)
                }
            }
            binding.rvRecommendedCourses.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            recommendedCourseList = Constants.recommendedCourseList
            helperFunctions.setupDotsIndicator(
                requireContext(),
                recommendedCourseList.size,
                binding.llDotsIndicatorRecommendedCourses
            )
            binding.rvRecommendedCourses.addOnScrollListener(object :
                RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    helperFunctions.updateDotsIndicator(
                        recyclerView,
                        binding.llDotsIndicatorRecommendedCourses
                    )
                }
            })
        })

        _binding?.progressBar?.visibility = View.VISIBLE
        _binding?.rvOurCourses?.visibility = View.GONE

        coursesCategoryViewModel.coursesCategory.observe(viewLifecycleOwner, Observer { category ->
            _binding?.progressBar?.visibility = View.GONE

            if (!category.isNullOrEmpty()) {
                Log.e("coursesCategor not", category.toString())
                _binding?.rvOurCourses?.visibility = View.VISIBLE
                listOurCoursesItem = category
                adapterOurCourses = OurCoursesAdapter(listOurCoursesItem!!, this)
                rvOurCourses.adapter = adapterOurCourses
                rvOurCourses.layoutManager = GridLayoutManager(context, 3, GridLayoutManager.HORIZONTAL, false)
                setupDotsIndicator(listOurCoursesItem!!.size, dotsIndicatorOurCourses, 6)
            }
        })
        coursesCategoryViewModel.fetchCoursesCategory()
        val filters = FindAllCourseInput(
            exam_type = Optional.Absent,
            is_recommended = Optional.present(true),
            course_status = Optional.present(listOf(CourseStatus.PUBLISHED))
        )

        coursesViewModel.fetchCourses(filters)

        listWhyCompetishun = Constants.listWhyCompetishun
        testimonials = Constants.testimonials

        adapter = TestimonialsAdapter(testimonials)
        adapterWhyCompetishun = WhyCompetishunAdapter(listWhyCompetishun)
        recyclerView.adapter = adapter
        rvWhyCompetishun.adapter = adapterWhyCompetishun
        recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rvWhyCompetishun.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        helperFunctions.setupDotsIndicator(
            requireContext(),
            testimonials.size,
            dotsIndicatorTestimonials
        )
        helperFunctions.setupDotsIndicator(
            requireContext(),
            listWhyCompetishun.size,
            dotsIndicatorWhyCompetishun
        )

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                helperFunctions.updateDotsIndicator(recyclerView, dotsIndicatorTestimonials)
            }
        })

        rvWhyCompetishun.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                helperFunctions.updateDotsIndicator(recyclerView, dotsIndicatorWhyCompetishun)
            }
        })
        setupClickListeners(view)


        val navigationView: NavigationView = view.findViewById(R.id.nv_navigationView)
        val headerView = navigationView.getHeaderView(0)
        val igClose: ImageView = headerView.findViewById(R.id.ig_close)

        igClose.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
        }

    }

    private fun setupClickListeners(view: View) {
        val clickableLayouts = listOf(
            R.id.clFYCourse to getString(R.string.full_year_courses),
//            R.id.clTestSeries to getString(R.string.test_series),
//            R.id.clRevisionCourses to getString(R.string.revision_courses),
//            R.id.clCrashCourses to getString(R.string.crash_courses),
//            R.id.clDistanceLearning to getString(R.string.distance_learning),
//            R.id.clDigitalBook to getString(R.string.digital_book),
        )

//        clickableLayouts.forEach { (id, logMessage) ->
//            view.findViewById<ConstraintLayout>(id).setOnClickListener {
//                val bundle = Bundle().apply {
//                    putString("clicked_view", logMessage)
//                }
//                Log.d("HomeFragment", logMessage)
//                findNavController().navigate(R.id.action_homeFragment_to_coursesFragment,bundle)
//            }
//        }

//        val clFYCourse = view.findViewById<ConstraintLayout>(R.id.clFYCourse)
//        clFYCourse.setOnClickListener {
//            Log.d("HomeFragment", "clFYCourse clicked")
//            findNavController().navigate(R.id.action_homeFragment_to_coursesFragment)
//        }

//        val clExploreCourse = view.findViewById<ConstraintLayout>(R.id.ctRecommendedCourse)
//        clExploreCourse.setOnClickListener {
//            findNavController().navigate(R.id.action_homeFragment_to_exploreFragment)
//        }
    }

    private fun setupDotsIndicator(
        itemCount: Int,
        dotsIndicator: LinearLayout,
        spanCount: Int = 6
    ) {
        val pageCount = (itemCount + spanCount - 1) / spanCount
        dotsIndicator.removeAllViews()
        for (i in 0 until pageCount) {
            val dot = ImageView(requireContext())
            dot.setImageResource(R.drawable.dot_inactive)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(4, 0, 4, 0)
            dot.layoutParams = params
            dotsIndicator.addView(dot)
        }
        updateDotsIndicator(null, dotsIndicator, spanCount)
    }


    private fun updateDotsIndicator(
        recyclerView: RecyclerView?,
        dotsIndicator: LinearLayout,
        spanCount: Int = 6
    ) {
        recyclerView?.let {
            val visiblePageIndex =
                (it.layoutManager as? GridLayoutManager)?.findFirstVisibleItemPosition()
                    ?.div(spanCount) ?: 0

            for (i in 0 until dotsIndicator.childCount) {
                val dot = dotsIndicator.getChildAt(i) as ImageView
                dot.setImageResource(
                    if (i == visiblePageIndex) R.drawable.dot_active
                    else R.drawable.dot_inactive
                )
            }
        }
    }

    override fun onCourseItemClick(course: GetAllCourseCategoriesQuery.GetAllCourseCategory) {
        val bundle = Bundle().apply {
            putString("course_name", course.name)
            putString("category_id", course.id)
            Log.e("courseane ", course.name)
            // Add other course details to the bundle if needed
        }
        findNavController().navigate(R.id.action_homeFragment_to_coursesFragment, bundle)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        drawerLayout.removeDrawerListener(toggle)
    }

}
