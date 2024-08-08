package com.student.competishun.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.student.competishun.R
import com.student.competishun.data.model.ExploreCourse
import com.student.competishun.databinding.FragmentCourseEmptyBinding
import com.student.competishun.ui.adapter.ExploreCourseAdapter

class CourseEmptyFragment : Fragment() {

    private var _binding: FragmentCourseEmptyBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // Handle arguments if any
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCourseEmptyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val exploreCourseList = listOf(
            ExploreCourse("Prakhar Integrated (Fast Lane-2) 2024-25", "12th class", "Full-Year", "Target 2025", "Ongoing", 10),
            ExploreCourse("Prakhar Integrated (Fast Lane-2) 2024-25", "Revision", "Full-Year", "Target 2025", "Completed", 100)
        )

        val adapter = ExploreCourseAdapter(exploreCourseList) { course ->
            findNavController().navigate(R.id.action_courseEmptyFragment_to_SubjectContentFragment)
        }

        binding.rvExploreCourses.adapter = adapter
        binding.rvExploreCourses.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
