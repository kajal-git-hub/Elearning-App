package com.student.competishun.ui.fragment

import RecommendedCoursesAdapter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo3.api.Optional
import com.student.competishun.R
import com.student.competishun.curator.type.CourseStatus
import com.student.competishun.curator.type.FindAllCourseInput
import com.student.competishun.data.model.PromoBannerModel
import com.student.competishun.databinding.FragmentPersonalDetailBinding
import com.student.competishun.databinding.FragmentRecommendViewDetailBinding
import com.student.competishun.ui.adapter.PromoBannerAdapter
import com.student.competishun.ui.viewmodel.CoursesViewModel
import com.student.competishun.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecommendViewDetail : Fragment() {

    private val coursesViewModel: CoursesViewModel by viewModels()


    private var _binding: FragmentRecommendViewDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecommendViewDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        coursesViewModel.courses.observe(viewLifecycleOwner, Observer { courses ->

                val adapter = courses?.let {
                    RecommendedCoursesAdapter(it) { selectedCourse ->
                        val bundle = Bundle().apply {
                            putString("course_id", selectedCourse.id)
                        }
                        findNavController().navigate(R.id.exploreFragment, bundle)
                    }
                }

                binding.rvRecommendedCourses.adapter = adapter
                binding.rvRecommendedCourses.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        })

        val filters = FindAllCourseInput(
            exam_type = Optional.Absent,
            is_recommended = Optional.present(true),
            course_status = Optional.present(listOf(CourseStatus.PUBLISHED)),
            limit = Optional.present(20)
        )

        coursesViewModel.fetchCourses(filters)


    }

}