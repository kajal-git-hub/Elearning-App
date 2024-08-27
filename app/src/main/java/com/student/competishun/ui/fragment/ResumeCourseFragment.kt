package com.student.competishun.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.student.competishun.R
import com.student.competishun.curator.MyCoursesQuery
import com.student.competishun.databinding.FragmentResumeCourseBinding
import com.student.competishun.ui.viewmodel.MyCoursesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResumeCourseFragment : Fragment() {

    private var _binding: FragmentResumeCourseBinding? = null
    private val binding get() = _binding!!
    private val myCourseViewModel: MyCoursesViewModel by viewModels()

    private val courseId: String? by lazy {
        arguments?.getString("course_Id")
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentResumeCourseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backIcon.setOnClickListener {
            requireActivity().onBackPressed()
        }

        Log.d("resumecourse", "courseId: $courseId")

        binding.clResumeCourseIcon2.setOnClickListener {
            findNavController().navigate(R.id.action_resumeCourseFragment_to_ScheduleFragment)
        }
        binding.clMathematics.setOnClickListener {
            findNavController().navigate(R.id.action_resumeCourseFragment_to_subjectContentFragment)
        }
        myCourse()
    }

    private fun myCourse() {
        myCourseViewModel.myCourses.observe(viewLifecycleOwner) { result ->
            result.onSuccess { data ->
                Log.d("resumecourse", data.toString())
                val course = data.myCourses.find { it.course.id == courseId }
                course?.let {
                    populateCourseData(it)
                } ?: run {
                    Toast.makeText(requireContext(), "Course not found", Toast.LENGTH_SHORT).show()
                }
            }.onFailure {
                Log.e("MyCoursesFail", it.message.toString())
                Toast.makeText(requireContext(), "Failed to load courses: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }

        myCourseViewModel.fetchMyCourses()
    }

    private fun populateCourseData(course: MyCoursesQuery.MyCourse) {
        binding.courseNameResumeCourse.text = course.course.name
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
