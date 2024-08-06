package com.student.competishun.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.apollographql.apollo3.api.Optional
import com.student.competishun.R
import com.student.competishun.databinding.FragmentReferenceBinding
import com.student.competishun.gatekeeper.type.UpdateUserInput
import com.student.competishun.ui.adapter.ExampleAdapter
import com.student.competishun.ui.main.MainActivity
import com.student.competishun.ui.viewmodel.UpdateUserViewModel
import com.student.competishun.utils.SharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ReferenceFragment : Fragment() {
    private var _binding: FragmentReferenceBinding? = null
    private val binding get() = _binding!!
    private val updateUserViewModel: UpdateUserViewModel by viewModels()
    private var currentStep = 2
    private var isItemSelected = false
    private lateinit var sharedPreferencesManager: SharedPreferencesManager

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
        sharedPreferencesManager = (requireActivity() as MainActivity).sharedPreferencesManager
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            handleBackPressed()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.RefNext.text = "Done"

        binding.RefNext.setOnClickListener {

            if (isItemSelected) {
                val updateUserInput = UpdateUserInput(
                    city = Optional.Present(sharedPreferencesManager.city),
                    fullName = Optional.Present(sharedPreferencesManager.name),
                    preparingFor = Optional.Present(sharedPreferencesManager.preparingFor),
                    reference = Optional.Present(sharedPreferencesManager.reference),
                    targetYear = Optional.Present(sharedPreferencesManager.targetYear)
                )

                updateUserViewModel.updateUser(updateUserInput)

            } else {
                Toast.makeText(context, "PleaFse select an option", Toast.LENGTH_SHORT).show()
            }
        }
        updateUserViewModel.updateUserResult.observe(viewLifecycleOwner, Observer { result ->
            if (result?.user != null) {
                // Handle successful response
                navigateToLoaderScreen()
                val user = result.user
                Log.e("gettingUserUpdate",result.user.fullName.toString())
            } else {
                Log.e("gettingUserUpdatefail",result.toString())
                Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show()
            }
        })

        Log.d("ExampleFragment", "Update successful:1")

        binding.RefBack.setOnClickListener {
            findNavController().navigateUp()
        }


        binding.etStartedText.text = stepTexts[currentStep]
        binding.etText.text = pageTexts[currentStep]

        val exampleAdapter = ExampleAdapter(
            dataList = dataSets[currentStep],
            currentStep = currentStep,
            spanCount = spanCount[currentStep]
        ) { selectedItem ->
            sharedPreferencesManager.reference = selectedItem
            Log.d("selectedItem",selectedItem)
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
        binding.refRecyclerview.startAnimation(slideInAnimation)
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
            findNavController().navigate(R.id.homeActivity)
        }, 5000)
    }


}