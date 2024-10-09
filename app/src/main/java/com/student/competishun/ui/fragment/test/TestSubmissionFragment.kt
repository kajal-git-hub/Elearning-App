package com.student.competishun.ui.fragment.test

import android.animation.ValueAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.student.competishun.R
import com.student.competishun.databinding.FragmentTestSubmissionBinding
import kotlin.math.cos
import kotlin.math.sin


class TestSubmissionFragment : Fragment() {

    private var _binding: FragmentTestSubmissionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTestSubmissionBinding.inflate(inflater, container, false)
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.PrimaryColor)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clickListener()
        binding.circularProgress.setMaxProgress(180.0)
        binding.circularProgress.setCurrentProgress(167.0)
        binding.circularProgress.setProgress(167.0, 180.0)
    }

    private fun clickListener() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
        _binding = null
    }

}