package com.student.competishun.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.student.competishun.R
import com.student.competishun.databinding.FragmentLoginBinding
import com.student.competishun.ui.main.MainActivity
import com.student.competishun.ui.viewmodel.GetOtpViewModel
import com.student.competishun.utils.SharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val otpViewModel: GetOtpViewModel by viewModels()
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private var countryCode: String? = null
    private var mobileNo: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        sharedPreferencesManager = (requireActivity() as MainActivity).sharedPreferencesManager
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupObservers()

        binding.btnVerify.setOnClickListener {
            handleVerifyButtonClick()
        }
    }

    private fun setupUI() {
        setupPhoneInput()
        setupTermsAndPrivacyText()
    }

    private fun setupPhoneInput() {
        binding.etEnterMob.apply {
            filters = arrayOf(InputFilter.LengthFilter(10))

            setOnFocusChangeListener { _, hasFocus ->
                binding.phoneInputLayout.setBackgroundResource(
                    if (hasFocus) R.drawable.rounded_homeeditext_clicked else R.drawable.rounded_homeditext_unclicked
                )
            }

            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    updateVerifyButtonState(s?.length == 10)
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }

    private fun updateVerifyButtonState(isEnabled: Boolean) {
        binding.btnVerify.setBackgroundColor(
            if (isEnabled) Color.parseColor("#3E3EF7") else Color.parseColor("#B5B3B3")
        )
    }

    private fun setupTermsAndPrivacyText() {
        val termsText = getString(R.string.terms_conditions)
        val privacyText = getString(R.string.privacy_policy)
        val fullText =
            getString(R.string.by_signing_up_you_agree_to_our_and, termsText, privacyText)
        val spannableString = SpannableString(fullText)

        spannableString.apply {
            setClickableSpan(termsText, termsClickableSpan)
            setClickableSpan(privacyText, privacyClickableSpan)
        }

        binding.tvTermsPrivacy.apply {
            text = spannableString
            movementMethod = LinkMovementMethod.getInstance()
        }
    }

    private fun SpannableString.setClickableSpan(text: String, clickableSpan: ClickableSpan) {
        val start = indexOf(text)
        val end = start + text.length
        if (start >= 0) {
            setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(
                ForegroundColorSpan(Color.parseColor("#3E3EF7")),
                start,
                end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    private val termsClickableSpan = object : ClickableSpan() {
        override fun onClick(widget: View) {
            Toast.makeText(
                requireContext(),
                getString(R.string.terms_conditions_clicked),
                Toast.LENGTH_SHORT
            ).show()
        }

        override fun updateDrawState(ds: android.text.TextPaint) {
            ds.isUnderlineText = false
        }
    }

    private val privacyClickableSpan = object : ClickableSpan() {
        override fun onClick(widget: View) {
            Toast.makeText(
                requireContext(),
                getString(R.string.privacy_policy_clicked),
                Toast.LENGTH_SHORT
            ).show()
        }

        override fun updateDrawState(ds: android.text.TextPaint) {
            ds.isUnderlineText = false
        }
    }

    private fun handleVerifyButtonClick() {
        val countryCode = binding.etCountryCode.text?.toString()
        val mobileNo = binding.etEnterMob.text?.toString()

        if (countryCode != null) {
            when {
                mobileNo.isNullOrEmpty() -> showToast("Please Enter mobile number")
                mobileNo.length != 10 -> showToast("Enter a valid 10-digit mobile number")
                mobileNo[0] < '6' -> showToast("Mobile number should be valid")
                else -> {
                    otpViewModel.getOtp(countryCode, mobileNo)
                    navigateToVerifyOtpFragment(countryCode, mobileNo)
                }
            }
        }
    }



    private fun navigateToVerifyOtpFragment(countryCode: String, mobileNo: String) {
        val bundle = Bundle().apply {
            putString("mobileNumber", mobileNo)
            putString("countryCode", countryCode)
            sharedPreferencesManager.mobileNo = mobileNo
            Log.d("shared numbve", sharedPreferencesManager.mobileNo.toString())
        }
        findNavController().navigate(R.id.action_loginFragment_to_verifyOTPFragment, bundle)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun setupObservers() {
        otpViewModel.otpResult.observe(viewLifecycleOwner) { result ->

            if (result == true ) {
                countryCode?.let { mobileNo?.let { it1 -> navigateToVerifyOtpFragment(it, it1) } }

            } else {
                showToast(result.toString())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
