package com.student.competishun.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.student.competishun.databinding.FragmentPrepForBinding
import com.student.competishun.ui.adapter.ExampleAdapter
import com.student.competishun.utils.Constants

class PrepForFragment : Fragment() {
    private var _binding: FragmentPrepForBinding? = null
    private val binding get() = _binding!!


    private var currentStep = 0
    private var isItemSelected = false

    private val dataSets = Constants.DATA_SETS
    private val pageTexts = Constants.PAGE_TEXTS
    private val stepTexts = Constants.STEP_TEXTS
    private val spanCount = listOf(2, 2, 1)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPrepForBinding.inflate(inflater, container, false)
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
            binding.PrepNext.setBackgroundResource(R.drawable.second_getstarteddone)

            if(selectedItem=="Others"){
                binding.etContentBox.visibility = View.VISIBLE
            }else{
                binding.etContentBox.visibility = View.GONE
                isItemSelected = true
            }
            updateButtonBackground()
        }

        binding.prepRecyclerview.apply {
            layoutManager = GridLayoutManager(context, spanCount[currentStep])
            adapter = exampleAdapter
        }

        startSlideInAnimation()

        updateButtonBackground()

        setupWordCounter()
    }

    private fun setupWordCounter() {
        binding.etContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed before text change
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val wordCount = s.toString().trim().split("\\s+".toRegex()).size
                binding.tvWordCounter.text = "$wordCount"+"/100"
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
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
        binding.clAnimConstraint.startAnimation(slideInAnimation)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
