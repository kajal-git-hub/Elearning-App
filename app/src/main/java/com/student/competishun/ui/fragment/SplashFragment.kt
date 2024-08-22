package com.student.competishun.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.student.competishun.R
import com.student.competishun.databinding.FragmentSplashBinding

class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment using View Binding
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showTemporarySplashScreen()
    }

    private fun showTemporarySplashScreen() {
        val tempView = LayoutInflater.from(requireContext()).inflate(R.layout.welcome_screen, null)

        (binding.root as ViewGroup).addView(tempView)

        Handler(Looper.getMainLooper()).postDelayed({
            (binding.root as ViewGroup).removeView(tempView)

            // Navigate to the WelcomeFragment
            findNavController().navigate(R.id.onWelcomeFragment)
        }, 3000) // Adjust the delay time as needed
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clean up the binding reference
        _binding = null
    }
}
