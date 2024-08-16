package com.student.competishun.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.student.competishun.R
import com.student.competishun.databinding.FragmentResumeCourseBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResumeCourseFragment : Fragment() {

    private var _binding: FragmentResumeCourseBinding? = null
    private val binding get() = _binding!!

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
        binding.clMathematics.setOnClickListener {
            findNavController().navigate(R.id.action_resumeCourseFragment_to_subjectContentFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}