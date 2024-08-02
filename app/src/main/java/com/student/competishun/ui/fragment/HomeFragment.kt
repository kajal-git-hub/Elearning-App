package com.student.competishun.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.constraintlayout.widget.ConstraintLayout
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
import com.google.android.material.navigation.NavigationView
import com.student.competishun.R
import com.student.competishun.curator.GetAllCourseCategoriesQuery
import com.student.competishun.curator.type.FindAllCourseInput
import com.student.competishun.data.model.Testimonial
import com.student.competishun.data.model.WhyCompetishun
import com.student.competishun.databinding.FragmentHomeBinding
import com.student.competishun.ui.adapter.OurCoursesAdapter
import com.student.competishun.ui.adapter.TestimonialsAdapter
import com.student.competishun.ui.adapter.WhyCompetishunAdapter
import com.student.competishun.ui.viewmodel.CoursesCategoryViewModel
import com.student.competishun.ui.viewmodel.CoursesViewModel
import com.student.competishun.utils.HelperFunctions
import com.student.competishun.utils.OnCourseItemClickListener
import dagger.hilt.android.AndroidEntryPoint

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
    private lateinit var toggle: ActionBarDrawerToggle
    private val coursesViewModel: CoursesViewModel by viewModels()
    private val coursesCategoryViewModel: CoursesCategoryViewModel by viewModels()
    private lateinit var rvOurCourses: RecyclerView
    private lateinit var dotsIndicatorOurCourses: LinearLayout
    private lateinit var adapterOurCourses: OurCoursesAdapter
    private  var listOurCoursesItem: List<GetAllCourseCategoriesQuery.GetAllCourseCategory>? = null


    private lateinit var helperFunctions: HelperFunctions


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
        dotsIndicatorOurCourses = view.findViewById(R.id.llDotsIndicatorOurCourses)

        rvOurCourses.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                updateDotsIndicator(recyclerView, dotsIndicatorOurCourses, 3)
            }
        })


        helperFunctions = HelperFunctions()

        drawerLayout = view.findViewById(R.id.drwaer_layout)
        val toolbar: MaterialToolbar = view.findViewById(R.id.topAppBar)

        toggle = ActionBarDrawerToggle(
            activity, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        recyclerView = view.findViewById(R.id.recyclerViewTestimonials)
        rvWhyCompetishun=view.findViewById(R.id.rvWhyCompetishun)
        dotsIndicatorTestimonials = view.findViewById(R.id.llDotsIndicator)
        dotsIndicatorWhyCompetishun = view.findViewById(R.id.llDotsIndicatorWhyCompetishun)

        coursesViewModel.courses.observe(viewLifecycleOwner, Observer { courses ->
            _binding?.tvBatchName?.text = courses?.firstOrNull()?.name
            Log.e("Coursesres",courses.toString())
            // Update UI with courses data
            // For example: binding.textView.text = courses?.firstOrNull()?.name ?: "No courses"
        })

        // Fetch courses when the view is created
        _binding?.progressBar?.visibility = View.VISIBLE
        _binding?.rvOurCourses?.visibility = View.GONE

        coursesCategoryViewModel.coursesCategory.observe(viewLifecycleOwner, Observer { category ->
            _binding?.progressBar?.visibility = View.GONE

            if (!category.isNullOrEmpty()) {
                Log.e("coursesCategor not",category.toString())
                _binding?.rvOurCourses?.visibility = View.VISIBLE
                listOurCoursesItem = category
                adapterOurCourses = OurCoursesAdapter(listOurCoursesItem!!,this)
                rvOurCourses.adapter = adapterOurCourses
                rvOurCourses.layoutManager = GridLayoutManager(context, 3, GridLayoutManager.HORIZONTAL, false)
                setupDotsIndicator(listOurCoursesItem!!.size, dotsIndicatorOurCourses, 3)
            }
            // Update UI with courses data
            // For example: binding.textView.text = courses?.firstOrNull()?.name ?: "No courses"
        })
        coursesCategoryViewModel.fetchCoursesCategory()
        val filters = FindAllCourseInput(
            exam_type = Optional.Absent,
            is_recommended = Optional.present(true)
        )



        coursesViewModel.fetchCourses(filters)

        coursesViewModel.courses.observe(viewLifecycleOwner, Observer { courses ->
            _binding?.tvBatchName?.text = courses?.firstOrNull()?.name
           _binding?.dicountPrice?.text = "₹"+ coursesViewModel.getDiscountDetails(coursesViewModel.courses.value!!.firstOrNull()?.price?.toInt()!!, coursesViewModel.courses.value!!.get(0).discount!!.toInt()).second.toString()
            _binding?.discountPerc?.text = coursesViewModel.getDiscountDetails(coursesViewModel.courses.value!!.firstOrNull()?.price!!.toInt(), coursesViewModel.courses.value!!.get(0).discount!!.toInt()).first.toString() + "% OFF"
        })

        // Fetch courses when the view is created
        listWhyCompetishun = listOf(
            WhyCompetishun("Competishun","IIT - JEE Cracked","NEET Cracked","https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"),
            WhyCompetishun("Competishun","IIT - JEE Cracked","NEET Cracked","https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"),
            WhyCompetishun("Competishun","IIT - JEE Cracked","NEET Cracked","https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4"),
            WhyCompetishun("Competishun","IIT - JEE Cracked","NEET Cracked","https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4")
        )
        testimonials = listOf(
            Testimonial("The classes are very good. Teachers explain topics very well. Must buy course for all aspiring students.", "Aman Sharma", "Class: 12th", "IIT JEE"),
            Testimonial("The classes are very good. Teachers explain topics very well. Must buy course for all aspiring students.", "Aman Sharma", "Class: 12th", "IIT JEE"),
            Testimonial("The classes are very good. Teachers explain topics very well. Must buy course for all aspiring students.", "Aman Sharma", "Class: 12th", "IIT JEE"),
            Testimonial("The classes are very good. Teachers explain topics very well. Must buy course for all aspiring students.", "Aman Sharma", "Class: 12th", "IIT JEE"),
            Testimonial("The classes are very good. Teachers explain topics very well. Must buy course for all aspiring students.", "Aman Sharma", "Class: 12th", "IIT JEE")
        )

        adapter = TestimonialsAdapter(testimonials)
        adapterWhyCompetishun = WhyCompetishunAdapter(listWhyCompetishun)
        recyclerView.adapter = adapter
        rvWhyCompetishun.adapter= adapterWhyCompetishun
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rvWhyCompetishun.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        helperFunctions.setupDotsIndicator(requireContext(),testimonials.size, dotsIndicatorTestimonials)
        helperFunctions.setupDotsIndicator(requireContext(),listWhyCompetishun.size, dotsIndicatorWhyCompetishun)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                helperFunctions.updateDotsIndicator(recyclerView,dotsIndicatorTestimonials)
            }
        })

        rvWhyCompetishun.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                helperFunctions.updateDotsIndicator(recyclerView,dotsIndicatorWhyCompetishun)
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

        val clExploreCourse = view.findViewById<ConstraintLayout>(R.id.ctRecommendedCourse)
        clExploreCourse.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_exploreFragment)
        }
    }
    private fun setupDotsIndicator(itemCount: Int, dotsIndicator: LinearLayout, spanCount: Int = 1) {
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

    private fun updateDotsIndicator(recyclerView: RecyclerView?, dotsIndicator: LinearLayout, spanCount: Int = 1) {
        recyclerView?.let {
            val visiblePageIndex = when (val layoutManager = it.layoutManager) {
                is LinearLayoutManager -> {
                    val totalScrollX = layoutManager.findFirstVisibleItemPosition()
                    val totalWidth = layoutManager.itemCount
                    val visiblePage = totalScrollX * layoutManager.itemCount / totalWidth
                    visiblePage
                }
                is GridLayoutManager -> {
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                    val visiblePage = firstVisibleItemPosition / spanCount
                    val totalPages = (layoutManager.itemCount + layoutManager.spanCount - 1) / layoutManager.spanCount
                    visiblePage.coerceIn(0, totalPages - 1)
                }
                else -> 0
            }

            for (i in 0 until dotsIndicator.childCount) {
                val dot = dotsIndicator.getChildAt(i) as ImageView
                val size = 16
                val params = LinearLayout.LayoutParams(size, size)
                params.setMargins(4, 0, 4, 0)
                dot.layoutParams = params
                dot.setImageResource(
                    if (i == visiblePageIndex) R.drawable.doc_active
                    else R.drawable.dot_inactive
                )
            }
        }
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCourseItemClick(course: GetAllCourseCategoriesQuery.GetAllCourseCategory) {
        val bundle = Bundle().apply {
            putString("course_name", course.name)
            // Add other course details to the bundle if needed
        }
        findNavController().navigate(R.id.action_homeFragment_to_coursesFragment, bundle)
    }
}