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
import com.student.competishun.type.UpdateUserInput
import com.student.competishun.databinding.FragmentOnBoardingBinding
import com.student.competishun.ui.adapter.ExampleAdapter
import com.student.competishun.ui.viewmodel.UpdateUserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnBoardingFragment : Fragment() {

    private var _binding: FragmentOnBoardingBinding? = null
    private val binding get() = _binding!!
    private var currentStep = 0
    var city = ""
    var name = ""
    private val updateUserViewModel: UpdateUserViewModel by viewModels()

    private val dataSets = listOf(
        listOf("ITT-JEE", "NEET", "Board", "UCET", "8th to 10th", "Others"),
        listOf("2025", "2026", "2027", "2028"),
        listOf("Friends/Family", "Social Media", "Advertisement", "Other"),
    )
    private val pageTexts= listOf(
        "2",
        "3",
        "4"
    )

    private val stepTexts = listOf(
        "Which exam are you \npreparing for? Please select",
        "What is your target year?",
        "How do you know about \nCompetishun?"
    )

    private val spanCount = listOf(2, 2, 1)

    private lateinit var adapter: ExampleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOnBoardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etNameConstraint.visibility = View.VISIBLE
        binding.clExamConstraint.visibility = View.GONE

        adapter = ExampleAdapter(dataSets[currentStep],currentStep,spanCount[currentStep])

        val city = binding.etEnterCityText.text.toString().trim()
        val name = binding.etEnterHereText.text.toString().trim()
        val updateUserInput = UpdateUserInput(
            city = Optional.Present(city),
            fullName = Optional.Present(name),
            preparingFor = Optional.Present("IIT-JEE"),
            reference = Optional.Present("Advertisement"),
            targetYear = Optional.Present(2024)
        )
        updateUserViewModel.updateUser(updateUserInput)
        updateUserViewModel.updateUserResult.observe(viewLifecycleOwner, Observer { result ->
            if (result != null && result.user != null) {
                val user = result.user
                Log.e("gettingUserUpdate",result.user.fullName.toString())
            } else {
                Log.e("gettingUserUpdate fail",result.toString())
                Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show()
            }
        })

        binding.examRecyclerview.layoutManager =
            GridLayoutManager(this@OnBoardingFragment.context, spanCount[currentStep])
        binding.examRecyclerview.adapter = adapter

        val slideInAnimation =
            AnimationUtils.loadAnimation(this@OnBoardingFragment.context, R.anim.slide_in_bottom)
        binding.clAnimConstraint.startAnimation(slideInAnimation)

        setUpTextWatchers()
        updateButtonBackground()
        Log.d("updated text", "$city $name")

        binding.btnGetSubmit2.setOnClickListener {
            Log.d("OnBoardingFragment", "Button clicked")
            when (currentStep) {
                0 -> {
                    binding.clExamConstraint.visibility = View.VISIBLE
                    binding.etNameConstraint.visibility = View.GONE
                    updateRecyclerViewData()
                    updateStepText()
                    updatePageText()
                }

                1 -> {
                    binding.clExamConstraint.visibility = View.VISIBLE
                    binding.etNameConstraint.visibility = View.GONE
                    updateRecyclerViewData()
                    updateStepText()
                    updatePageText()
                }

                2 -> {
                    binding.clExamConstraint.visibility = View.VISIBLE
                    binding.etNameConstraint.visibility = View.GONE
                    updateRecyclerViewData()
                    updateStepText()
                    updatePageText()
                    binding.btnGetSubmit2.text = "Done"
                    binding.btnGetSubmit2.setOnClickListener {
                        // on hold
                    }
                }
            }
            currentStep++
            binding.clAnimConstraint.startAnimation(slideInAnimation)
        }

        binding.btnGetSubmit1.setOnClickListener {
            findNavController().navigate(R.id.action_OnBoardingFragment_to_loginFragment)
        }
    }
    private fun updateStepText() {
        if (currentStep < stepTexts.size) {
            binding.etStartedText.text = stepTexts[currentStep]
        }
    }
    private fun updatePageText() {
        if(currentStep<pageTexts.size){
            binding.etText.text = pageTexts[currentStep]
        }
    }


    private fun updateRecyclerViewData() {
        if (currentStep < dataSets.size) {
            val newSpanCount = if (currentStep < spanCount.size) spanCount[currentStep] else 1
            adapter.updateData(dataSets[currentStep], currentStep, newSpanCount)
            binding.examRecyclerview.layoutManager = GridLayoutManager(this@OnBoardingFragment.context, newSpanCount)
        }
    }

    private fun setUpTextWatchers() {
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

    private fun updateButtonBackground() {
        val text1 = binding.etEnterHereText.text.toString().trim()
        val text2 = binding.etEnterCityText.text.toString().trim()

        Log.d("OnBoardingFragment", "etEnterHereText: $text1, etEnterCityText: $text2")

        if (text1.isNotEmpty() && text2.isNotEmpty()) {
            binding.btnGetSubmit2.setBackgroundResource(R.drawable.second_getstarteddone)
        } else {
            binding.btnGetSubmit2.setBackgroundResource(R.drawable.second_getstarted)
        }
         city  = text2
         name = text1
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
