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
import com.student.competishun.utils.Constants


@AndroidEntryPoint
class ReferenceFragment : Fragment() {
    private var _binding: FragmentReferenceBinding? = null
    private val binding get() = _binding!!
    private val updateUserViewModel: UpdateUserViewModel by viewModels()
    private var currentStep = 2
    private var isItemSelected = false
    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    private val dataSets = Constants.DATA_SETS
    private val pageTexts = Constants.PAGE_TEXTS
    private val stepTexts = Constants.STEP_TEXTS
    private val spanCount = listOf(2, 2, 1)

    private var selectedItem: String? = null

    private var SharedSelectedItem: String?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReferenceBinding.inflate(inflater, container, false)

        sharedPreferencesManager = (requireActivity() as MainActivity).sharedPreferencesManager

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            handleBackPressed()
        }

        selectedItem = sharedPreferencesManager.reference

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        restoreSelectedItem()

        binding.RefNext.text = "Done"

        binding.RefNext.setOnClickListener {
            sharedPreferencesManager.reference = SharedSelectedItem

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
                Toast.makeText(context, "Please select an option", Toast.LENGTH_SHORT).show()
            }
        }

        updateUserViewModel.updateUserResult.observe(viewLifecycleOwner, Observer { result ->
            if (result?.user != null) {
                navigateToLoaderScreen()
                Log.e("gettingUserUpdate", result.user.fullName.toString())
            } else {
                Log.e("gettingUserUpdatefail", result.toString())
                Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show()
            }
        })

        binding.RefBack.setOnClickListener {
            findNavController().popBackStack(R.id.TargetFragment, false)
        }

        binding.etStartedText.text = stepTexts[currentStep]
        binding.etText.text = pageTexts[currentStep]

        setupRecyclerView()

        startSlideInAnimation()

        updateButtonBackground()
    }

    private fun restoreSelectedItem() {
        Log.d("SelectedItemRestore", "Selected Item: $selectedItem")
        selectedItem = sharedPreferencesManager.reference
        if (!selectedItem.isNullOrEmpty()) {
            isItemSelected = true
        } else {
            isItemSelected = false
        }
        updateButtonBackground()
    }

    override fun onResume() {
        super.onResume()
        updateButtonBackground()
    }

    private fun setupRecyclerView() {
        val exampleAdapter = ExampleAdapter(
            dataList = dataSets[currentStep],
            currentStep = currentStep,
            spanCount = spanCount[currentStep],
            selectedItem = sharedPreferencesManager.reference
        ) { selectedItem ->
            isItemSelected = true
            this.selectedItem = selectedItem
            SharedSelectedItem = selectedItem
            sharedPreferencesManager.reference = selectedItem // Save selected item
            binding.RefNext.setBackgroundResource(R.drawable.second_getstarteddone)
            updateButtonBackground()
        }

        binding.refRecyclerview.apply {
            layoutManager = GridLayoutManager(context, spanCount[currentStep])
            adapter = exampleAdapter
        }
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
        findNavController().popBackStack(R.id.TargetFragment, false)
    }

//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        outState.putString("SharedSelectedItem", SharedSelectedItem)
//    }

    private fun navigateToLoaderScreen() {
        binding.root.removeAllViews()
        val processingView = layoutInflater.inflate(R.layout.loader_screen, binding.root, false)
        binding.root.addView(processingView)

        Handler(Looper.getMainLooper()).postDelayed({
            findNavController().navigate(R.id.homeActivity)
        }, 5000)
    }
}
