package com.student.competishun.ui.fragment.test

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.student.competishun.R
import com.student.competishun.databinding.FragmentTestStatusBinding
import com.student.competishun.databinding.FragmentViewSolutionBinding

class TestStatusFragment : Fragment() {

    private var _binding: FragmentTestStatusBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTestStatusBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}