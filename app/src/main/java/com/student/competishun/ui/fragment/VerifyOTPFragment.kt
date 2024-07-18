package com.student.competishun.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.student.competishun.R
import com.student.competishun.databinding.FragmentVerifyBinding

class VerifyOTPFragment : Fragment() {

    private var _binding: FragmentVerifyBinding? = null
    private val binding get() = _binding!!

    private var mobileNumber: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVerifyBinding.inflate(inflater, container, false)
        return binding.root
    }

//    override fun onResume() {
//        super.onResume()
//        navigateBack()
//    }

//    private fun navigateBack() {
//        findNavController().navigate(R.id.action_verifyOTPFragment_to_loginFragment)
//    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.etArrowLeft.setOnClickListener {
            findNavController().navigate(R.id.action_verifyOTPFragment_to_loginFragment)
        }

        mobileNumber = arguments?.getString("mobileNumber")

        Log.d("VerifyOTPFragment", "Mobile number: $mobileNumber")

        setupOtpInputs()
        setupVerificationCodeText()
    }

    private fun setupOtpInputs() {
        val otpBoxes = listOf(
            binding.otpBox1,
            binding.otpBox2,
            binding.otpBox3,
            binding.otpBox4
        )

        otpBoxes.forEachIndexed { index, editText ->
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1 && index < otpBoxes.size - 1) {
                        otpBoxes[index + 1].requestFocus()
                    } else if (s?.length == 0 && index > 0) {
                        otpBoxes[index - 1].requestFocus()
                    }

                    if (otpBoxes.all { it.text.length == 1 }) {
                        binding.btnVerify.setBackgroundColor(Color.parseColor("#3E3EF7"))
                        binding.btnVerify.setOnClickListener {
                            navigateToNextScreen()
                        }
                    }
                }
            })

            editText.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN && editText.text.isEmpty() && index != 0) {
                    otpBoxes[index - 1].requestFocus()
                    return@setOnKeyListener true
                }
                false
            }
        }
    }

    private fun setupVerificationCodeText() {
        mobileNumber?.let { phoneNumber ->
            val fullText = "An verification code has been sent to your \nmail ID $phoneNumber"
            val start = fullText.indexOf(phoneNumber)

            if (start != -1) {
                val end = start + phoneNumber.length
                val spannableString = SpannableString(fullText)

                spannableString.setSpan(
                    ForegroundColorSpan(Color.parseColor("#3E3EF7")),
                    start,
                    end,
                    SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                spannableString.setSpan(
                    object : ClickableSpan() {
                        override fun onClick(widget: View) {
                            Toast.makeText(
                                requireContext(),
                                "Phone number clicked",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    start,
                    end,
                    SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                binding.etVerificationCodeText.text = spannableString
                binding.etVerificationCodeText.movementMethod = LinkMovementMethod.getInstance()
            } else {
                Log.d("VerifyOTPFragment", "Phone number not found in text: $phoneNumber")
            }
        }
    }

    private fun navigateToNextScreen() {
        findNavController().navigate(R.id.action_verifyOTPFragment_to_onBoardingFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
