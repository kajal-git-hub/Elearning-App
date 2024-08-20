package com.student.competishun.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.student.competishun.R
import com.student.competishun.databinding.FragmentOnBoardingBinding
import com.student.competishun.ui.main.MainActivity
import com.student.competishun.ui.viewmodel.UserViewModel
import com.student.competishun.utils.SharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnBoardingFragment : Fragment() {

    private var _binding: FragmentOnBoardingBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnBoardingBinding.inflate(inflater, container, false)
        sharedPreferencesManager = (requireActivity() as MainActivity).sharedPreferencesManager

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            handleBackPressed()
        }

        return binding.root
    }

    private fun handleBackPressed() {
        findNavController().navigate(R.id.loginFragment)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeUserDetails()
        userViewModel.fetchUserDetails()

        setupTextWatchers()

        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_OnBoardingFragment_to_loginFragment)
        }

        binding.NextOnBoarding.setOnClickListener {
            if (isCurrentStepValid()) {
                saveNameAndCity()
                findNavController().navigate(R.id.action_OnBoardingFragment_to_prepForFragment)
            } else {
                Toast.makeText(context, "Please select a name and city", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeUserDetails() {
        userViewModel.userDetails.observe(viewLifecycleOwner) { result ->
            result.onSuccess { data ->
                val name = data.getMyDetails.fullName
                val city = data.getMyDetails.userInformation.city

                if (!name.isNullOrEmpty()) {
                    binding.etEnterHereText.setText(name)
                }
                if (!city.isNullOrEmpty()) {
                    binding.etEnterCityText.setText(city)
                }

                sharedPreferencesManager.name = name
                sharedPreferencesManager.city = city

                updateNextButtonState()

            }.onFailure { exception ->
                Toast.makeText(requireContext(), "Error fetching details: ${exception.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupTextWatchers() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateNextButtonState()
            }
        }
        binding.etEnterHereText.addTextChangedListener(textWatcher)
        binding.etEnterCityText.addTextChangedListener(textWatcher)
    }

    private fun updateNextButtonState() {
        val isNameValid = binding.etEnterHereText.text.toString().trim().length >= 3
        val isCityValid = binding.etEnterCityText.text.toString().trim().length >= 3

        if (isNameValid && isCityValid) {
            binding.NextOnBoarding.setBackgroundResource(R.drawable.second_getstarteddone)
        } else {
            binding.NextOnBoarding.setBackgroundResource(R.drawable.second_getstarted)
        }
    }

    private fun isCurrentStepValid(): Boolean {
        val name = binding.etEnterHereText.text.toString().trim()
        val city = binding.etEnterCityText.text.toString().trim()
        return name.length >= 3 && city.length >= 3
    }

    private fun saveNameAndCity() {
        val name = binding.etEnterHereText.text.toString().trim()
        val city = binding.etEnterCityText.text.toString().trim()

        sharedPreferencesManager.name = name
        sharedPreferencesManager.city = city

//        userViewModel.updateUserDetails(name, city)

        Log.d("OnBoardingFragment", "Name and City saved: $name, $city")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        updateNextButtonState()
    }
}
