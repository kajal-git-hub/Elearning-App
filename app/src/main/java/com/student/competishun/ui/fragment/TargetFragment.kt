package com.student.competishun.ui.fragment

import android.os.Bundle
import android.util.Log
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
import com.student.competishun.databinding.FragmentTargetBinding
import com.student.competishun.ui.adapter.ExampleAdapter
import com.student.competishun.ui.main.MainActivity
import com.student.competishun.utils.SharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TargetFragment : Fragment() {
    private var _binding: FragmentTargetBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    private var currentStep = 1
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // Handle arguments if needed
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTargetBinding.inflate(inflater, container, false)
        sharedPreferencesManager = (requireActivity() as MainActivity).sharedPreferencesManager

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            handleBackPressed()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.TargetBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.TargetNext.setOnClickListener {
            if (isItemSelected) {
                findNavController().navigate(R.id.action_TargetFragment_to_reference)
            } else {
                Toast.makeText(context, "Please select an option", Toast.LENGTH_SHORT).show()
            }
        }

        // Set the text for step and page
        binding.etStartedText.text = stepTexts[currentStep]
        binding.etText.text = pageTexts[currentStep]

        val exampleAdapter = ExampleAdapter(
            dataList = dataSets[currentStep],
            currentStep = currentStep,
            spanCount = spanCount[currentStep]
        ) { selectedItem ->
            Log.d("TargetFragment", "Selected item: $selectedItem")

            // Save target year to SharedPreferences
            sharedPreferencesManager.targetYear = selectedItem.toIntOrNull()
            isItemSelected = true

            // Update button background
            updateButtonBackground()
        }

        binding.targetRecyclerview.apply {
            layoutManager = GridLayoutManager(context, spanCount[currentStep])
            adapter = exampleAdapter
        }

        startSlideInAnimation()
        updateButtonBackground()
    }

    private fun updateButtonBackground() {
        binding.TargetNext.setBackgroundResource(
            if (isItemSelected) R.drawable.second_getstarteddone
            else R.drawable.second_getstarted
        )
    }

    private fun startSlideInAnimation() {
        val slideInAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_in_bottom)
        binding.targetRecyclerview.startAnimation(slideInAnimation)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun handleBackPressed() {
        findNavController().navigateUp()
    }
}
