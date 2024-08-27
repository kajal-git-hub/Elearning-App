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
import com.student.competishun.databinding.FragmentResumeCourseBinding
import com.student.competishun.ui.viewmodel.MyCoursesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResumeCourseFragment : Fragment() {

    private var _binding: FragmentResumeCourseBinding? = null
    private val binding get() = _binding!!
    private val myCourseViewModel: MyCoursesViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentResumeCourseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Handle view logic here
        binding.backIcon.setOnClickListener {
            requireActivity().onBackPressed()
        }
        myCourse()
        binding.clResumeCourseIcon2.setOnClickListener {
            findNavController().navigate(R.id.action_resumeCourseFragment_to_ScheduleFragment)
        }
        binding.clMathematics.setOnClickListener {
            findNavController().navigate(R.id.action_resumeCourseFragment_to_subjectContentFragment)
        }
    }

    fun myCourse(){
        myCourseViewModel.myCourses.observe(viewLifecycleOwner) { result ->
            result.onSuccess { data ->
                Log.d("resumecourse",data.toString())
            }.onFailure {
                Log.e("MyCoursesFail",it.message.toString())
                Toast.makeText(requireContext(), "Failed to load courses: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }

        myCourseViewModel.fetchMyCourses()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
