package com.student.competishun.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.apollographql.apollo3.api.Optional
import com.student.competishun.R
import com.student.competishun.databinding.FragmentOnBoardingBinding
import com.student.competishun.type.UpdateUserInput
import com.student.competishun.ui.adapter.ExampleAdapter
import com.student.competishun.ui.viewmodel.UpdateUserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnBoardingFragment : Fragment() {

    private var _binding: FragmentOnBoardingBinding? = null
    private val binding get() = _binding!!
    private var currentStep = 0
    private var city = ""
    private var name = ""
    private var list = mutableListOf<String>()
    private val updateUserViewModel: UpdateUserViewModel by viewModels()

    private val dataSets = listOf(
        listOf("IIT-JEE", "NEET", "Board", "UCET", "8th to 10th", "Others"),
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

    private lateinit var adapter: ExampleAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOnBoardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupInitialStep()
        setupRecyclerView()
        setupTextWatchers()
        setupButtonClickListeners()
        startSlideInAnimation()
        observeViewModel()
    }

    private fun setupInitialStep() {
        binding.etNameConstraint.visibility = View.VISIBLE
        binding.clExamConstraint.visibility = View.GONE
        name = binding.etEnterHereText.text.toString().trim()
        city = binding.etEnterCityText.text.toString().trim()
    }

    private fun setupRecyclerView() {
        adapter = ExampleAdapter(
            dataSets[currentStep],
            currentStep,
            spanCount[currentStep],
            onItemSelected = { selectedItem ->
                Log.d("Adapter", "Selected item: $selectedItem")
                list.add(selectedItem)
                Log.d("List",list.toString())
            }
        )

        binding.examRecyclerview.layoutManager = GridLayoutManager(context, spanCount[currentStep])
        binding.examRecyclerview.adapter = adapter
    }

    private fun setupTextWatchers() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateButtonBackground()
            }
        }
        binding.etEnterHereText.addTextChangedListener(textWatcher)
        binding.etEnterCityText.addTextChangedListener(textWatcher)
    }

    private fun setupButtonClickListeners() {
        binding.btnGetSubmit2.setOnClickListener {
            handleNextButtonClick()
        }
        binding.btnGetSubmit1.setOnClickListener {
            currentStep = (currentStep - 1).coerceAtLeast(0)
            handleBackButtonClick()
        }
    }

    private fun handleBackButtonClick() {
        Log.d("currentStep", currentStep.toString())
        when (currentStep) {
            0 -> {
                setupInitialStep()
            }
            else -> {
                showExamSelection()
            }
        }
        startSlideInAnimation()
    }

    private fun handleNextButtonClick() {
        Log.d("handleNextButtonClick", currentStep.toString())
        when (currentStep) {
            0 -> {
                showExamSelection()
                currentStep++
            }
            1 -> {
                showExamSelection()
                currentStep++
            }
            2 -> {
                showExamSelection()
                binding.btnGetSubmit2.text = "Done"
                binding.btnGetSubmit2.setOnClickListener {
                    handleSubmitButtonClick()
                }
            }
        }
        startSlideInAnimation()
    }

    private fun showExamSelection() {
        binding.clExamConstraint.visibility = View.VISIBLE
        binding.etNameConstraint.visibility = View.GONE
        updateRecyclerViewData()
        updateStepText()
        updatePageText()
    }

    private fun startSlideInAnimation() {
        val slideInAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_in_bottom)
        binding.clAnimConstraint.startAnimation(slideInAnimation)
    }

    private fun updateStepText() {
        if (currentStep < stepTexts.size) {
            binding.etStartedText.text = stepTexts[currentStep]
        }
    }

    private fun updatePageText() {
        if (currentStep < pageTexts.size) {
            binding.etText.text = pageTexts[currentStep]
        }
    }

    private fun updateRecyclerViewData() {
        Log.d("currentStep", currentStep.toString())
        if (currentStep < dataSets.size) {
            val newSpanCount = spanCount.getOrNull(currentStep) ?: 1
            adapter.updateData(dataSets[currentStep], currentStep, newSpanCount)
            binding.examRecyclerview.layoutManager = GridLayoutManager(context, newSpanCount)
        }
    }

    private fun updateButtonBackground() {
        name = binding.etEnterHereText.text.toString().trim()
        city = binding.etEnterCityText.text.toString().trim()
        val isButtonEnabled = name.isNotEmpty() && city.isNotEmpty()
        binding.btnGetSubmit2.setBackgroundResource(
            if (isButtonEnabled) R.drawable.second_getstarteddone
            else R.drawable.second_getstarted
        )
    }

    private fun observeViewModel() {
        updateUserViewModel.updateUserResult.observe(viewLifecycleOwner, Observer { result ->
            result?.user?.let { user ->
                Log.e("gettingUserUpdate", user.fullName.toString())
                findNavController().navigate(R.id.action_OnBoardingFragment_to_HomeActivity)

            } ?: run {
                Log.e("gettingUserUpdate fail", result.toString())
                Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun handleSubmitButtonClick() {
        val selectedItem = adapter.getSelectedItem()

        Log.d("handleSubmitButtonClick", "Selected item: $selectedItem")

        if (city.isBlank()) {
            Toast.makeText(context, "Please enter your city", Toast.LENGTH_SHORT).show()
            return
        }

        if (name.isBlank()) {
            Toast.makeText(context, "Please enter your name", Toast.LENGTH_SHORT).show()
            return
        }

        if (list.size < 3) {
            Toast.makeText(context, "Please select all required options", Toast.LENGTH_SHORT).show()
            return
        }

        val preparingFor = list.getOrNull(0)
        val targetYear = list.getOrNull(1)?.toIntOrNull()
        val reference = list.getOrNull(2)

        if (preparingFor.isNullOrBlank() || targetYear == null || reference.isNullOrBlank()) {
            Toast.makeText(context, "Please complete all selections", Toast.LENGTH_SHORT).show()
            return
        }

        val updateUserInput = UpdateUserInput(
            city = Optional.Present(city),
            fullName = Optional.Present(name),
            preparingFor = Optional.Present(preparingFor),
            targetYear = Optional.Present(targetYear),
            reference = Optional.Present(reference)
        )

        list.clear()
        Log.d("updateUserInput", updateUserInput.toString())
        updateUserViewModel.updateUser(updateUserInput)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
