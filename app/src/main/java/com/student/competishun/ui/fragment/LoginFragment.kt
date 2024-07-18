package com.student.competishun.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.student.competishun.R
import com.student.competishun.databinding.FragmentLoginBinding
import com.student.competishun.ui.viewmodel.GetOtpViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val otpViewModel: GetOtpViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupObservers()

        binding.btnVerify.setOnClickListener {
            val countryCode = binding.etCountryCode.text.toString()
            val mobileNo = binding.etEnterMob.text.toString()

            if (mobileNo.length == 10) {
                // Proceed to fetch OTP or navigate depending on your logic
                otpViewModel.getOtp(countryCode, mobileNo)

                // Navigate to VerifyOTPFragment and pass mobileNo via arguments
                val bundle = Bundle().apply {
                    putString("mobileNumber", mobileNo)
                }
                findNavController().navigate(R.id.action_loginFragment_to_verifyOTPFragment, bundle)
            } else if (mobileNo.length > 10) {
                Toast.makeText(requireContext(), "Mobile number should be 10 digits", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Enter a valid mobile number", Toast.LENGTH_SHORT).show()
            }
        }



    }

    private fun setupUI() {
        val phoneInputLayout = binding.phoneInputLayout
        val etEnterMob = binding.etEnterMob

        etEnterMob.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                phoneInputLayout.setBackgroundResource(R.drawable.rounded_homeeditext_clicked)
            }
        }

        etEnterMob.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val length = s?.length ?: 0
                if (length == 10) {
                    binding.btnVerify.setBackgroundColor(Color.parseColor("#3E3EF7"))
                } else {
                    binding.btnVerify.setBackgroundColor(Color.parseColor("#B5B3B3"))
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        val termsText = getString(R.string.terms_conditions)
        val privacyText = getString(R.string.privacy_policy)
        val fullText =
            getString(R.string.by_signing_up_you_agree_to_our_and, termsText, privacyText)

        val spannableString = SpannableString(fullText)

        val termsClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                Toast.makeText(requireContext(),
                    getString(R.string.terms_conditions_clicked), Toast.LENGTH_SHORT).show()
            }

            override fun updateDrawState(ds: android.text.TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }

        val privacyClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                Toast.makeText(requireContext(),
                    getString(R.string.privacy_policy_clicked), Toast.LENGTH_SHORT).show()
            }

            override fun updateDrawState(ds: android.text.TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }

        spannableString.setSpan(
            termsClickableSpan,
            fullText.indexOf(termsText),
            fullText.indexOf(termsText) + termsText.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            ForegroundColorSpan(Color.parseColor("#3E3EF7")),
            fullText.indexOf(termsText),
            fullText.indexOf(termsText) + termsText.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            privacyClickableSpan,
            fullText.indexOf(privacyText),
            fullText.indexOf(privacyText) + privacyText.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            ForegroundColorSpan(Color.parseColor("#3E3EF7")),
            fullText.indexOf(privacyText),
            fullText.indexOf(privacyText) + privacyText.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.tvTermsPrivacy.text = spannableString
        binding.tvTermsPrivacy.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun setupObservers() {
        otpViewModel.otpResult.observe(viewLifecycleOwner) { result ->
            if (result == true) {
                findNavController().navigate(R.id.action_loginFragment_to_verifyOTPFragment)
            } else {
                Toast.makeText(requireContext(), "OTP Verification Failed", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
