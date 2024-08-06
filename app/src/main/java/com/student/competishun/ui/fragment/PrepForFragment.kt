package com.student.competishun.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.addCallback
import androidx.media3.common.util.Log
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.student.competishun.R
import com.student.competishun.databinding.FragmentPrepForBinding
import com.student.competishun.ui.adapter.ExampleAdapter
import com.student.competishun.ui.main.MainActivity
import com.student.competishun.utils.SharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PrepForFragment : Fragment() {
    private var _binding: FragmentPrepForBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private var currentStep = 0
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
        _binding = FragmentPrepForBinding.inflate(inflater, container, false)
        sharedPreferencesManager = (requireActivity() as MainActivity).sharedPreferencesManager

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            handleBackPressed()
        }
        return binding.root
    }
    private fun handleBackPressed() {
        findNavController().navigate(R.id.onboardingFragment)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.PrepBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.PrepNext.setOnClickListener {
            if (isItemSelected) {
                findNavController().navigate(R.id.action_PrepForFragment_to_TargetFragment)
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
            Log.d("selectedItem",selectedItem)
            sharedPreferencesManager.preparingFor = selectedItem
            binding.PrepNext.setBackgroundResource(R.drawable.second_getstarteddone)
        }

        binding.prepRecyclerview.apply {
            layoutManager = GridLayoutManager(context, spanCount[currentStep])
            adapter = exampleAdapter
        }

        startSlideInAnimation()

        updateButtonBackground()
    }

    private fun updateButtonBackground() {
        if (isItemSelected) {
            binding.PrepNext.setBackgroundResource(R.drawable.second_getstarteddone)
        } else {
            binding.PrepNext.setBackgroundResource(R.drawable.second_getstarted)
        }
    }

    private fun startSlideInAnimation() {
        val slideInAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_in_bottom)
        binding.prepRecyclerview.startAnimation(slideInAnimation)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
