package xyz.penpencil.competishun.ui.fragment

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.tasks.Task
import dagger.hilt.android.AndroidEntryPoint
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.databinding.FragmentVerifyBinding
import xyz.penpencil.competishun.download.MySMSBroadcastReceiver
import xyz.penpencil.competishun.ui.main.HomeActivity
import xyz.penpencil.competishun.ui.main.MainActivity
import xyz.penpencil.competishun.ui.viewmodel.GetOtpViewModel
import xyz.penpencil.competishun.ui.viewmodel.UserViewModel
import xyz.penpencil.competishun.ui.viewmodel.VerifyOtpViewModel
import xyz.penpencil.competishun.utils.SharedPreferencesManager


@AndroidEntryPoint
class VerifyOTPFragment : Fragment() {

    private var _binding: FragmentVerifyBinding? = null
    private val binding get() = _binding!!
    private val verifyOtpViewModel: VerifyOtpViewModel by viewModels()
    private val otpViewModel: GetOtpViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    private var mobileNumber: String? = null
    private var countryCode: String? = null
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private lateinit var otpBoxes: List<EditText>
    private lateinit var countDownTimer: CountDownTimer

    private val REQ_USER_CONSENT=200

    private val mySMSBroadcastReceiver : MySMSBroadcastReceiver by lazy { MySMSBroadcastReceiver() }

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
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(mMessageReceiver, IntentFilter("otp-receiver"))
        setupViews()
        setupOtpInputs()
        setupVerificationCodeText()
        startTimer()
        observeViewModel()

        startAutoFillOtp()
    }

    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val otp = intent.getStringExtra("OTP")
            otp?.let { getOtpFromMessage(it) }
        }
    }

    private fun getOtpFromMessage(message: String?) {
        Log.d("message",message.toString())
        val otpPattern = Regex("(\\d{4})")
        val matchResult = otpPattern.find(message.toString())
        val otp = matchResult?.value

        binding.otpBox1.setText(otp?.substring(0,1))
        binding.otpBox2.setText(otp?.substring(1,2))
        binding.otpBox3.setText(otp?.substring(2,3))
        binding.otpBox4.setText(otp?.substring(3,4))
    }

    private fun startAutoFillOtp(){
        val client= SmsRetriever.getClient(requireContext())
        client.startSmsUserConsent(null)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_USER_CONSENT) {
            if (resultCode == RESULT_OK && data != null) {
                val message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                getOtpFromMessage(message)
            }
        }
    }

    private fun registerBroadcastReceiver(){
        ContextCompat.registerReceiver(requireContext(), mySMSBroadcastReceiver, IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }



    private fun startSMSRetrieverClient() {
        val client = SmsRetriever.getClient(requireActivity())
        val task: Task<Void> = client.startSmsRetriever()
        task.addOnSuccessListener { void ->
            mySMSBroadcastReceiver.addListener(object : MySMSBroadcastReceiver.OTPReceiveListener {
                override fun onOTPReceived(otp: String?) { otp?.let { getOtpFromMessage(it) } }
                override fun onOTPTimeOut() {}
            })
            registerBroadcastReceiver()
        }.addOnFailureListener { e -> }

        mySMSBroadcastReceiver.addListener(object : MySMSBroadcastReceiver.OTPReceiveListener {
            override fun onOTPReceived(otp: String?) { otp?.let { getOtpFromMessage(it) } }
            override fun onOTPTimeOut() {}
        })

        registerBroadcastReceiver()
    }

    private fun setupViews() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        startSMSRetrieverClient()
        binding.etArrowLeft.setOnClickListener {
            it.findNavController().popBackStack()
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                }
            })

        binding.btnVerify.setOnClickListener {
            if (otpBoxes.all { it.text.length == 1 }) {
                checkOtpAndNavigate()
            } else {
                Toast.makeText(requireContext(), "Please enter a valid OTP", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun observeViewModel() {
        Log.d("shared number", sharedPreferencesManager.mobileNo.toString())
        if (!sharedPreferencesManager.mobileNo.isNullOrEmpty())
            mobileNumber = sharedPreferencesManager.mobileNo

        verifyOtpViewModel.verifyOtpResult.observe(viewLifecycleOwner) { result ->
            if (result != null) {
                sharedPreferencesManager.userId = result.user?.id
                sharedPreferencesManager.accessToken = result.accessToken
                sharedPreferencesManager.refreshToken = result.refreshToken
                changeOtpBoxesBackground(R.drawable.otp_edit_text_background)
                binding.etEnterOtpText.text = "Enter the OTP to continue"
                binding.etEnterOtpText.setTextColor(Color.parseColor("#726C6C"))

                userViewModel.fetchUserDetails()

                userViewModel.userDetails.observe(viewLifecycleOwner) { userDetailsResult ->
                    userDetailsResult.onSuccess { data ->
                        val userDetails = data.getMyDetails

                        if (userDetails.userInformation.address?.city!=null && userDetails.userInformation.reference!=null && userDetails.userInformation.targetYear!=null && userDetails.userInformation.preparingFor!=null && userDetails.fullName!=null && userDetails.userInformation.fatherName==null) {

                            navigateToHomeActivity(userDetails.id)
                        }
                        else if (userDetails.courses.isNotEmpty()) {
                            navigateToMyCourse()
                        }
                        else {
                            // Store necessary data in SharedPreferencesManager
                            sharedPreferencesManager.mobileNo = userDetails.mobileNumber
                            navigateToHome()
                        }
                    }.onFailure { exception ->
                        Toast.makeText(
                            requireContext(),
                            "Otp is not verified try again later",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Invalid OTP", Toast.LENGTH_SHORT).show()
                changeOtpBoxesBackground(R.drawable.opt_edit_text_bg_incorrect)
                binding.etEnterOtpText.text = "Incorrect OTP"
                binding.btnVerify.setBackgroundColor(Color.parseColor(getString(R.string.verify_btn_default)))
                binding.etEnterOtpText.setTextColor(Color.parseColor(getString(R.string.Error)))
            }
        }
    }

    private fun navigateToMyCourse() {
        val intent = Intent(requireContext(), HomeActivity::class.java).apply {
            putExtra("navigateTo", "CourseEmptyFragment")
        }
        startActivity(intent)
        requireActivity().finish()
    }

    private fun navigateToHomeActivity(userId:String) {
       sharedPreferencesManager.userId = userId
        Log.e("userIdvero",userId.toString())
        val intent = Intent(requireContext(), HomeActivity::class.java).apply {
            putExtra("userId", userId)
        }
        startActivity(intent)
        requireActivity().finish()
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
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

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
                    sendOTPRequest()
                    startAutoFillOtp()
                }
            }
        }
        countDownTimer.start()
    }

    private fun sendOTPRequest() {

        otpViewModel.getOtp(countryCode.toString(), mobileNumber.toString())
        otpViewModel.otpResult.observe(viewLifecycleOwner) { result ->

            if (result == true ) {
//                countryCode?.let { mobileNo?.let { it1 -> navigateToVerifyOtpFragment(it, it1) } }
                Log.d("OTPsENT","OTP has been sent")
            } else {
                Log.d("OTPnOTSent","OTP not get yet")
            }
        }
    }


    override fun onResume() {
        super.onResume()
        registerBroadcastReceiver()
        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        unRegister()
        countDownTimer.cancel()
    }

    override fun onStop() {
        unRegister()
        super.onStop()
    }

    private fun unRegister(){
        try {
            LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(mMessageReceiver);
            requireContext().unregisterReceiver(mySMSBroadcastReceiver)
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unRegister()
    }
}

