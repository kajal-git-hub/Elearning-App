package com.student.competishun.ui.fragment.test

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.student.competishun.R
import com.student.competishun.databinding.FragmentTestSubmissionBinding


class TestSubmissionFragment : Fragment() {

    private var _binding: FragmentTestSubmissionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTestSubmissionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.blue_3E3EF7)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Glide.with(requireContext())
            .load(R.drawable.ic_success_loading)
            .into(binding.loading)
        loadData()
        clickListener()
        binding.circularProgress.setMaxProgress(180.0)
        binding.circularProgress.setCurrentProgress(167.0)
        binding.circularProgress.setProgress(167.0, 180.0)
    }

    private fun loadData(){
        Handler(Looper.getMainLooper()).postDelayed({
            binding.mainContainer.visibility = View.VISIBLE
            binding.loading.visibility = View.GONE
        }, 2000)
    }

    private fun clickListener() {
        binding.close.setOnClickListener {
            it.findNavController().navigate(R.id.action_testSubmissionFragment_to_testDashboardFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
        _binding = null
    }

}