package com.student.competishun.ui.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
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
import androidx.activity.OnBackPressedCallback
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.student.competishun.R
import com.student.competishun.databinding.FragmentVerifyBinding
import com.student.competishun.ui.main.MainActivity
import com.student.competishun.ui.viewmodel.VerifyOtpViewModel
import com.student.competishun.utils.SharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class VerifyOTPFragment : Fragment() {

    private var _binding: FragmentVerifyBinding? = null
    private val binding get() = _binding!!
    private val verifyOtpViewModel: VerifyOtpViewModel by viewModels()

    private var mobileNumber: String? = null
    private var countryCode: String? = null
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private lateinit var otpBoxes: List<EditText>
    private lateinit var countDownTimer: CountDownTimer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVerifyBinding.inflate(inflater, container, false)
        sharedPreferencesManager = (requireActivity() as MainActivity).sharedPreferencesManager
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mobileNumber = arguments?.getString("mobileNumber")
        countryCode = arguments?.getString("countryCode")

        setupViews()
        setupOtpInputs()
        setupVerificationCodeText()
        startTimer()
        observeViewModel()
    }

    private fun setupViews() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.etArrowLeft.setOnClickListener {
            findNavController().navigate(R.id.action_verifyOTPFragment_to_loginFragment)
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_verifyOTPFragment_to_loginFragment)
            }
        })

        binding.btnVerify.setOnClickListener {
            if (otpBoxes.all { it.text.length == 1 }) {
                checkOtpAndNavigate()
            } else {
                Toast.makeText(requireContext(), "Please enter a valid OTP", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeViewModel() {
        Log.d("shared number", sharedPreferencesManager.mobileNo.toString())
        if (!sharedPreferencesManager.mobileNo.isNullOrEmpty()) mobileNumber = sharedPreferencesManager.mobileNo

        verifyOtpViewModel.verifyOtpResult.observe(viewLifecycleOwner) { result ->
            if (result != null) {
                Log.e("Success in Verify", "${result.user} ${result.refreshToken} ${result.accessToken}")
                sharedPreferencesManager.userId = result.user?.id
                changeOtpBoxesBackground(R.drawable.otp_edit_text_background)
                binding.etEnterOtpText.text = "Enter the OTP to continue"
                binding.etEnterOtpText.setTextColor(Color.parseColor("#726C6C"))
                navigateToHome()
            } else {
                Log.e("FailureBefore", "${result} $mobileNumber")
                Toast.makeText(requireContext(), "Invalid OTP", Toast.LENGTH_SHORT).show()
                changeOtpBoxesBackground(R.drawable.opt_edit_text_bg_incorrect)
                binding.etEnterOtpText.text = "Incorrect OTP"
                binding.btnVerify.setBackgroundColor(Color.parseColor(getString(R.string.verify_btn_default)))
                binding.etEnterOtpText.setTextColor(Color.parseColor(getString(R.string.Error)))
            }
        }
    }

    private fun navigateToHome() {
        findNavController().navigate(R.id.action_verifyOTPFragment_to_onBoardingFragment)
    }

    private fun checkOtpAndNavigate() {
        val otp = otpBoxes.joinToString("") { it.text.toString() }
        if (otp.isNotEmpty() && countryCode != null && mobileNumber != null) {
            verifyOtpViewModel.verifyOtp(countryCode!!, mobileNumber!!, otp.toInt())
        } else {
            Toast.makeText(requireContext(), "Please enter a valid OTP", Toast.LENGTH_SHORT).show()
        }
    }

    private fun changeOtpBoxesBackground(backgroundResource: Int) {
        otpBoxes.forEach {
            it.setBackgroundResource(backgroundResource)
        }
    }

    private fun setupOtpInputs() {
        otpBoxes = listOf(
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
                    }

                    if (otpBoxes.all { it.text.length == 1 }) {
                        binding.btnVerify.setBackgroundColor(Color.parseColor(getString(R.string._3e3ef7)))
                    } else {
                        binding.btnVerify.setBackgroundColor(Color.parseColor(getString(R.string.dadada)))
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
            val fullText = "A verification code has been sent to your \nphone no. $phoneNumber"
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
                        override fun updateDrawState(ds: android.text.TextPaint) {
                            ds.isUnderlineText = false
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

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                binding.etTimeText.text = "${secondsRemaining}s"
            }

            override fun onFinish() {
                binding.etEnterOtpText.text = "Didn’t receive any OTP?"
                binding.etWaitText.visibility = View.GONE
                binding.etTimeText.visibility = View.GONE
                binding.etResendText.visibility = View.VISIBLE

                binding.etResendText.setOnClickListener {
                    // Handle resend OTP action
                    binding.etEnterOtpText.text = "Enter the OTP to continue"
                    binding.etWaitText.visibility = View.VISIBLE
                    binding.etTimeText.visibility = View.VISIBLE
                    binding.etResendText.visibility = View.GONE
                    startTimer()  // Restart the timer
                }
            }
        }
        countDownTimer.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        countDownTimer.cancel()
    }
}

