package com.student.competishun.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.student.competishun.R
import com.student.competishun.databinding.FragmentOnBoardingBinding
import com.student.competishun.databinding.FragmentReferenceBinding
import com.student.competishun.databinding.FragmentTargetBinding
import com.student.competishun.ui.adapter.ExampleAdapter

class ReferenceFragment : Fragment() {
    private var _binding: FragmentReferenceBinding? = null
    private val binding get() = _binding!!


    private var currentStep = 2
    private var isItemSelected = false

    private val dataSets = listOf(
        listOf("IIT-JEE", "NEET", "Board", "UCET", "Others"),
        listOf("2025", "2026", "2027", "2028"),
        listOf("Friends/Family", "Social Media", "Advertisement", "Other")
    )

    private val pageTexts = listOf("2", "3", "4")
    private val stepTexts = listOf(
        "Which exam are you \npreparing for? Please select",
        "What is your target year?",
        "How do you know about \nCompetishun?"
    )
    private val spanCount = listOf(2, 2, 1)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReferenceBinding.inflate(inflater, container, false)

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            handleBackPressed()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.RefNext.text = "Done"

        binding.RefBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.RefNext.setOnClickListener {
            if (isItemSelected) {
                findNavController().navigate(R.id.action_ReferenceFragment_to_HomeActivity)
            } else {
                Toast.makeText(context, "Please select an option", Toast.LENGTH_SHORT).show()
            }
        }

        binding.etStartedText.text = stepTexts[currentStep]
        binding.etText.text = pageTexts[currentStep]

        val exampleAdapter = ExampleAdapter(
            dataList = dataSets[currentStep],
            currentStep = currentStep,
            spanCount = spanCount[currentStep]
        ) { selectedItem ->
            isItemSelected = true
            binding.RefNext.setBackgroundResource(R.drawable.second_getstarteddone)
        }

        binding.refRecyclerview.apply {
            layoutManager = GridLayoutManager(context, spanCount[currentStep])
            adapter = exampleAdapter
        }

        startSlideInAnimation()

        updateButtonBackground()
    }

    private fun updateButtonBackground() {
        if (isItemSelected) {
            binding.RefNext.setBackgroundResource(R.drawable.second_getstarteddone)
        } else {
            binding.RefNext.setBackgroundResource(R.drawable.second_getstarted)
        }
    }

    private fun startSlideInAnimation() {
        val slideInAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_in_bottom)
        binding.clAnimConstraint.startAnimation(slideInAnimation)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun handleBackPressed() {
        findNavController().navigateUp()
    }


    private fun navigateToLoaderScreen() {
        binding.root.removeAllViews()
        val processingView = layoutInflater.inflate(R.layout.loader_screen, binding.root, false)
        binding.root.addView(processingView)

        Handler(Looper.getMainLooper()).postDelayed({
//            findNavController().navigate(R.id.action_OnBoardingFragment_to_HomeActivity)
        }, 5000)
    }


}