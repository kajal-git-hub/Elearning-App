package com.student.competishun.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import androidx.navigation.fragment.findNavController
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.student.competishun.R
import com.student.competishun.data.model.Testimonial
import com.student.competishun.databinding.FragmentHomeBinding
import com.student.competishun.databinding.FragmentLoginBinding
import com.student.competishun.ui.adapter.TestimonialsAdapter
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private lateinit var viewPager: ViewPager2
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TestimonialsAdapter
    private lateinit var dotsIndicator: WormDotsIndicator
    private lateinit var navController: NavController
    private lateinit var testimonials: List<Testimonial>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager = view.findViewById(R.id.viewPagerTestimonials)
        dotsIndicator = view.findViewById(R.id.worm_dots_indicator)

        val testimonials = listOf(
            Testimonial("The classes are very good. Teachers explain topics very well. Must buy course for all aspiring students.", "Aman Sharma", "Class: 12th", "IIT JEE"),
            Testimonial("The classes are very good. Teachers explain topics very well. Must buy course for all aspiring students.", "Aman Sharma", "Class: 12th", "IIT JEE"),
            Testimonial("The classes are very good. Teachers explain topics very well. Must buy course for all aspiring students.", "Aman Sharma", "Class: 12th", "IIT JEE"),
            Testimonial("The classes are very good. Teachers explain topics very well. Must buy course for all aspiring students.", "Aman Sharma", "Class: 12th", "IIT JEE")
        )


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
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}