package com.student.competishun.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.apollographql.apollo3.api.Optional
import com.student.competishun.R
import com.student.competishun.databinding.FragmentOnBoardingBinding
import com.student.competishun.gatekeeper.type.UpdateUserInput
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
        handleBackPress()
    }

    override fun onPause() {
        super.onPause()
        saveState()
    }

    override fun onResume() {
        super.onResume()
        restoreState()
    }

    private fun setupInitialStep() {
        binding.etNameConstraint.visibility = View.VISIBLE
        binding.clExamConstraint.visibility = View.GONE
        name = binding.etEnterHereText.text.toString().trim()
        city = binding.etEnterCityText.text.toString().trim()
        updateButtonBackground()
    }


    private fun setupRecyclerView() {
        adapter = ExampleAdapter(
            dataSets[currentStep],
            currentStep,
            spanCount[currentStep],
            onItemSelected = { selectedItem ->
                Log.d("Adapter", "Selected item: $selectedItem")
                list.add(selectedItem)
                Log.d("List", list.toString())
                updateButtonBackground()
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
        if (currentStep == 0){
            binding.btnback.isEnabled = false
            binding.btnback.backgroundTintMode = null
            binding.btnback.backgroundTintList = null
        binding.btnback.setOnClickListener {
            handleBackButtonClick()
        }
        }
    }
    private fun saveState(){
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", 0)
        val editor = sharedPreferences.edit()
        editor.putInt("currentStep", currentStep)
        editor.putString("name", name)
        editor.putString("city", city)
        editor.putStringSet("selectedItems", list.toSet())
        editor.apply()
    }
    private fun restoreState() {
        val sharedPreferences = requireContext().getSharedPreferences("OnBoardingPrefs", 0)
        currentStep = sharedPreferences.getInt("currentStep", 0)
        name = sharedPreferences.getString("name", "") ?: ""
        city = sharedPreferences.getString("city", "") ?: ""
        list = sharedPreferences.getStringSet("selectedItems", emptySet())?.toMutableList() ?: mutableListOf()
        Log.d("restoredData", "currentStep: $currentStep, name: $name, city: $city, list: $list")
    }


    private fun handleBackButtonClick() {
        if (currentStep != 0) {
            currentStep -= 1
        }
        Log.d("currentStep", currentStep.toString())
        when (currentStep) {
            0 -> {
                showExamSelection(currentStep)
                binding.btnback.isEnabled = false
                binding.btnback.backgroundTintMode = null
            }
            1->{
                showExamSelection()
            }
            -1->{
                setupInitialStep()
                binding.etStartedText.text = resources.getString(R.string.let_s_get_started)
                binding.etText.text = "1"
            }
            else -> {
                findNavController().navigate(R.id.action_OnBoardingFragment_to_loginFragment)
            }
        }
        startSlideInAnimation()
    }

    private fun handleNextButtonClick() {
        Log.d("handleNextButtonClick", currentStep.toString())
        if (!isCurrentStepValid()) {
            Toast.makeText(context, "Please Select the option", Toast.LENGTH_SHORT).show()
            return
        }
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
        updateButtonBackground()
        saveState()
    }

    private fun isCurrentStepValid(): Boolean {
        return when (currentStep) {
            0 -> {
                name = binding.etEnterHereText.text.toString().trim()
                city = binding.etEnterCityText.text.toString().trim()
                name.isNotEmpty() && city.isNotEmpty()
            }
            1 -> {
                list.size >= 1
            }
            2 -> {
                list.size >= 2
            }
            else -> true
        }
    }

    private fun showExamSelection(currentStep: Int = this.currentStep) {
        if(currentStep==0){
            binding.etContentBox.visibility = View.VISIBLE
            binding.clExamConstraint.visibility = View.VISIBLE
            binding.etNameConstraint.visibility = View.GONE
            binding.btnback.isEnabled = false
            binding.btnback.backgroundTintMode = null
            updateRecyclerViewData()
            updateStepText()
            updatePageText()
        }else{
            binding.etContentBox.visibility = View.GONE
            binding.clExamConstraint.visibility = View.VISIBLE
            binding.etNameConstraint.visibility = View.GONE
            updateRecyclerViewData()
            updateStepText()
            updatePageText()
        }
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
        val isButtonEnabled = when (currentStep) {
            0 -> {
                val currentName = binding.etEnterHereText.text.toString().trim()
                val currentCity = binding.etEnterCityText.text.toString().trim()
                currentName.isNotEmpty() && currentCity.isNotEmpty()
            }
            1 -> {
                list.size >= 1
            }
            2 -> {
                list.size >= 2
            }
            else -> true
        }

        binding.btnGetSubmit2.setBackgroundResource(
            if (isButtonEnabled) R.drawable.second_getstarteddone
            else R.drawable.second_getstarted
        )

    }

    private fun observeViewModel() {
        updateUserViewModel.updateUserResult.observe(viewLifecycleOwner) { result ->
            result?.let { user ->
                Log.e("updateUserResponse", result.toString())
                Log.e("gettingUserUpdate", user.user?.fullName.toString())
                Toast.makeText(context, "User update successful", Toast.LENGTH_SHORT).show()
                navigateToLoaderScreen()
            } ?: run {
                Log.e("gettingUserUpdate fail", result.toString())
                Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleSubmitButtonClick() {
        if (!isCurrentStepValid()) {
            Toast.makeText(context, "Please complete all selections", Toast.LENGTH_SHORT).show()
            return
        }

        val preparingFor = list.getOrNull(0)
        val targetYear = list.getOrNull(1)?.toIntOrNull()
        val reference = list.getOrNull(2)

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

    private fun navigateToLoaderScreen() {
        binding.root.removeAllViews()
        val processingView = layoutInflater.inflate(R.layout.loader_screen, binding.root, false)
        binding.root.addView(processingView)

        Handler(Looper.getMainLooper()).postDelayed({
            findNavController().navigate(R.id.action_OnBoardingFragment_to_HomeActivity)
        }, 5000)
    }

    private fun handleBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (currentStep == 0 && (name.isEmpty() && city.isEmpty())) {
                    Toast.makeText(context, "Please fill your details before proceeding.", Toast.LENGTH_SHORT).show()
                } else {
                    currentStep -= 1
                    handleBackButtonClick()
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
