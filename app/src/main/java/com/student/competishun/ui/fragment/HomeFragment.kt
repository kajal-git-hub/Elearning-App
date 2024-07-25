package com.student.competishun.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.student.competishun.R
import com.student.competishun.data.model.Testimonial
import com.student.competishun.databinding.FragmentHomeBinding
import com.student.competishun.ui.adapter.TestimonialsAdapter

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var viewPager: ViewPager2
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TestimonialsAdapter
    private lateinit var dotsIndicator: WormDotsIndicator
    private lateinit var navController: NavController
    private lateinit var testimonials: List<Testimonial>
    private lateinit var recyclerView: RecyclerView
    private lateinit var dotsIndicator: LinearLayout
    private lateinit var adapter: TestimonialsAdapter
    private lateinit var testimonials: List<Testimonial>
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
        dotsIndicator = view.findViewById(R.id.llDotsIndicator)

        testimonials = listOf(
            Testimonial("The classes are very good. Teachers explain topics very well. Must buy course for all aspiring students.", "Aman Sharma", "Class: 12th", "IIT JEE"),
            Testimonial("The classes are very good. Teachers explain topics very well. Must buy course for all aspiring students.", "Aman Sharma", "Class: 12th", "IIT JEE"),
            Testimonial("The classes are very good. Teachers explain topics very well. Must buy course for all aspiring students.", "Aman Sharma", "Class: 12th", "IIT JEE"),
            Testimonial("The classes are very good. Teachers explain topics very well. Must buy course for all aspiring students.", "Aman Sharma", "Class: 12th", "IIT JEE"),
            Testimonial("The classes are very good. Teachers explain topics very well. Must buy course for all aspiring students.", "Aman Sharma", "Class: 12th", "IIT JEE")
        )

        adapter = TestimonialsAdapter(testimonials)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        setupDotsIndicator()

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                updateDotsIndicator()
            }
        })
        val adapter = TestimonialsAdapter(testimonials)
        viewPager.adapter = adapter
        dotsIndicator.attachTo(viewPager)
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

        val clExploreCourse = view.findViewById<ConstraintLayout>(R.id.clExploreCourceButton)
        clExploreCourse.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_exploreFragment)
        }
    }



    private fun setupDotsIndicator() {
        dotsIndicator.removeAllViews()
        for (i in testimonials.indices) {
            val dot = ImageView(requireContext())
            dot.setImageResource(R.drawable.dot_inactive) // Your inactive dot drawable
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(4, 0, 4, 0)
            dot.layoutParams = params
            dotsIndicator.addView(dot)
        }
        updateDotsIndicator()
    }

    private fun updateDotsIndicator() {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        val position = layoutManager.findFirstVisibleItemPosition()

        for (i in 0 until dotsIndicator.childCount) {
            val dot = dotsIndicator.getChildAt(i) as ImageView
            dot.setImageResource(
                if (i == position) R.drawable.doc_active // Your active dot drawable
                else R.drawable.dot_inactive // Your inactive dot drawable
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}