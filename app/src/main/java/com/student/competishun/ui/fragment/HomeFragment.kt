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
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.student.competishun.R
import com.student.competishun.data.model.Testimonial
import com.student.competishun.data.model.WhyCompetishun
import com.student.competishun.databinding.FragmentHomeBinding
import com.student.competishun.ui.adapter.TestimonialsAdapter
import com.student.competishun.ui.adapter.WhyCompetishunAdapter
import com.student.competishun.utils.HelperFunctions
import com.student.competishun.ui.viewmodel.CoursesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
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

    private lateinit var helperFunctions: HelperFunctions


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



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
        coursesViewModel.fetchCourses()
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

    }

    private fun setupClickListeners(view: View) {
        val clickableLayouts = listOf(
            R.id.clFYCourse to getString(R.string.full_year_courses),
            R.id.clTestSeries to getString(R.string.test_series),
            R.id.clRevisionCourses to getString(R.string.revision_courses),
            R.id.clCrashCourses to getString(R.string.crash_courses),
            R.id.clDistanceLearning to getString(R.string.distance_learning),
            R.id.clDigitalBook to getString(R.string.digital_book),
        )

        clickableLayouts.forEach { (id, logMessage) ->
            view.findViewById<ConstraintLayout>(id).setOnClickListener {
                val bundle = Bundle().apply {
                    putString("clicked_view", logMessage)
                }
                Log.d("HomeFragment", logMessage)
                findNavController().navigate(R.id.action_homeFragment_to_coursesFragment,bundle)
            }
        }

        val clFYCourse = view.findViewById<ConstraintLayout>(R.id.clFYCourse)
        clFYCourse.setOnClickListener {
            Log.d("HomeFragment", "clFYCourse clicked")
            findNavController().navigate(R.id.action_homeFragment_to_coursesFragment)
        }

        val clExploreCourse = view.findViewById<ConstraintLayout>(R.id.ctRecommendedCourse)
        clExploreCourse.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_exploreFragment)
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}