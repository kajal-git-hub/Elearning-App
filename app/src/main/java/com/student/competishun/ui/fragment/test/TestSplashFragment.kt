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
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.databinding.FragmentTestSplashBinding


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
//            val message = it.getString("MESSAGE", "Best of luck!")
            //binding.message.text = message
            isTestStart = it.getBoolean("IS_TEST_START", false)
            if (isTestStart){
                binding.message.visibility = View.GONE
                binding.message1.visibility = View.VISIBLE
            }else{
                binding.message.visibility = View.VISIBLE
                binding.message1.visibility = View.GONE
            }

        }
        launchWhenResumed {
            delay(3000)
            if (isTestStart) {
                view.findNavController().navigate(R.id.action_testSplashFragment_to_testFragment)
            }else{
                view.findNavController().navigate(R.id.action_testSplashFragment_to_testSubmissionFragment)
            }
        }
        Glide.with(binding.loader).load(R.drawable.loader).into(binding.loader)
    }

    override fun onResume() {
        super.onResume()
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color._white_F6F6FF)
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