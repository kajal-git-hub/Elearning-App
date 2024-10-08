package com.student.competishun.ui.fragment.test

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import com.student.competishun.R
import com.student.competishun.databinding.FragmentTestDetailBinding
import com.student.competishun.databinding.FragmentTestSplashBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class TestSplashFragment : Fragment() {

    private var _binding: FragmentTestSplashBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTestSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var isTestStart = false
        arguments?.let {
            val message = it.getString("MESSAGE", "Best of luck!")
            binding.message.text = message
            isTestStart = it.getBoolean("IS_TEST_START", false)

        }
        launchWhenResumed {
            delay(3000)
            if (isTestStart) {
                view.findNavController().navigate(R.id.action_testSplashFragment_to_testFragment)
            }else{
                view.findNavController().navigate(R.id.action_testSplashFragment_to_testSubmissionFragment)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.home_bg)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
        _binding = null
    }

    private fun Fragment.launchWhenResumed(block: suspend () -> Unit) {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                block()
            }
        }
    }
}