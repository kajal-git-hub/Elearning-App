package com.student.competishun.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.student.competishun.R
import com.student.competishun.databinding.FragmentAboutUsBinding
import com.student.competishun.databinding.FragmentContactUsBinding
import com.student.competishun.databinding.FragmentHomeBinding

class ContactUsFragment : Fragment() {


    private var _binding: FragmentContactUsBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentContactUsBinding.inflate(inflater, container, false)
        return binding.root
    }

}