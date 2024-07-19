package com.student.competishun.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.student.competishun.R
import com.student.competishun.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        retainInstance = true
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

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
        binding.btnVerify.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_verifyOTPFragment)
        }

        val termsText = "Terms & Conditions"
        val privacyText = "Privacy Policy"
        val fullText = "By signing up, you agree to our $termsText \nand $privacyText"

        val spannableString = SpannableString(fullText)

        val termsClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                Toast.makeText(requireContext(), "Terms & Conditions Clicked", Toast.LENGTH_SHORT).show()
            }

            override fun updateDrawState(ds: android.text.TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }

        val privacyClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                Toast.makeText(requireContext(), "Privacy Policy Clicked", Toast.LENGTH_SHORT).show()
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
        binding.tvTermsPrivacy.movementMethod = android.text.method.LinkMovementMethod.getInstance()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
