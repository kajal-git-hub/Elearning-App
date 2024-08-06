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
import androidx.navigation.fragment.findNavController
import com.student.competishun.R
import com.student.competishun.databinding.FragmentOnBoardingBinding
import com.student.competishun.ui.main.MainActivity
import com.student.competishun.utils.SharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnBoardingFragment : Fragment() {

    private var _binding: FragmentOnBoardingBinding? = null
    private val binding get() = _binding!!
    private var moveForward: Boolean = false
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
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
        setupTextWatchers()
        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_OnBoardingFragment_to_loginFragment)
        }

        binding.NextOnBoarding.setOnClickListener {
            if (isCurrentStepValid()) {
                findNavController().navigate(R.id.action_OnBoardingFragment_to_prepForFragment)
            } else {
                Toast.makeText(context, "Please select a name and city", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupTextWatchers() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val name = binding.etEnterHereText.text.toString().trim()
                val city = binding.etEnterCityText.text.toString().trim()

                if (name.isNotEmpty() && name.length >= 3 && city.isNotEmpty() && city.length >= 3) {
                    binding.NextOnBoarding.setBackgroundResource(R.drawable.second_getstarteddone)
                } else {
                    binding.NextOnBoarding.setBackgroundResource(R.drawable.second_getstarted)
                }
            }
        }
        binding.etEnterHereText.addTextChangedListener(textWatcher)
        binding.etEnterCityText.addTextChangedListener(textWatcher)
    }

    private fun isCurrentStepValid(): Boolean {
        val name = binding.etEnterHereText.text.toString().trim()
        val city = binding.etEnterCityText.text.toString().trim()
        Log.e("updateusername",name)
        sharedPreferencesManager.name = name
        sharedPreferencesManager.city = city
        moveForward = name.isNotEmpty() && city.isNotEmpty()  && name.length >= 3 && city.isNotEmpty()
        return moveForward
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
