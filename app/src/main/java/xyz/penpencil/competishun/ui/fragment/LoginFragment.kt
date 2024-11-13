package xyz.penpencil.competishun.ui.fragment

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.telephony.TelephonyManager
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
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.PasswordCredential
import androidx.credentials.PublicKeyCredential
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.identity.GetPhoneNumberHintIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import xyz.penpencil.competishun.BuildConfig
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.databinding.FragmentLoginBinding
import xyz.penpencil.competishun.ui.main.HomeActivity
import xyz.penpencil.competishun.ui.main.MainActivity
import xyz.penpencil.competishun.ui.viewmodel.GetOtpViewModel
import xyz.penpencil.competishun.ui.viewmodel.UserViewModel
import xyz.penpencil.competishun.ui.viewmodel.VerifyOtpViewModel
import xyz.penpencil.competishun.utils.DoubleClickListener
import xyz.penpencil.competishun.utils.SharedPreferencesManager
import java.io.File
import java.util.Locale


@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var TAG = "GoogleFragment"
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val otpViewModel: GetOtpViewModel by viewModels()
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private var countryCode: String? = null
    private val verifyOtpViewModel: VerifyOtpViewModel by viewModels()
    private var mobileNo: String? = null

    private val userViewModel: UserViewModel by viewModels()

    val request: GetPhoneNumberHintIntentRequest = GetPhoneNumberHintIntentRequest.builder().build()

    private val phoneNumberHintIntentResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        try {
            val phoneNumber =
                Identity.getSignInClient(requireActivity()).getPhoneNumberFromIntent(result.data)
            val formattedPhoneNumber = removeCountryCode(requireContext(), phoneNumber)
            binding.etEnterMob.setText(formattedPhoneNumber)
            binding.etEnterMob.isFocusableInTouchMode = true
            binding.etEnterMob.isEnabled = true
            binding.etEnterMob.requestFocus()
            binding.etEnterMob.setSelection(binding.etEnterMob.text.length)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to retrieve phone number ${e.message}")
        }
    }

    private fun removeCountryCode(context: Context, phoneNumber: String): String {
        val telephonyManager =
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val countryCodeIso = telephonyManager.networkCountryIso.uppercase(Locale.getDefault())
        val countryCodePrefix = getCountryDialCode(countryCodeIso)
        return if (phoneNumber.startsWith(countryCodePrefix)) {
            phoneNumber.removePrefix(countryCodePrefix)
        } else {
            phoneNumber
        }
    }

    private fun getCountryDialCode(isoCode: String): String {
        val countryDialCodes = mapOf(
            "US" to "+1",
            "IN" to "+91",
            "GB" to "+44",
            "CA" to "+1",
            // Add more country codes as needed
        )

        return countryDialCodes[isoCode] ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        sharedPreferencesManager = (requireActivity() as MainActivity).sharedPreferencesManager

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupObservers()

        binding.etEnterMob.setOnClickListener(DoubleClickListener {
            if (binding.etEnterMob.text.isNullOrEmpty()) {
                retrievePhoneNumberHint()
            }
        })

        binding.etHelpText.setOnClickListener {
            val phoneNumber = "8888000021"
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$phoneNumber")
            startActivity(intent)
        }

        binding.btnVerify.setOnClickListener {
            handleVerifyButtonClick()
        }
        val account = GoogleSignIn.getLastSignedInAccount(requireContext())
        if (account != null) {
            Log.d("accountfount", "User is already signed in: ${account.email}")
        } else {
            Log.d("account", "No account signed in.")
        }

        binding.roundedConstraintView.setOnClickListener { googleCredential() }
    }

    private fun View.showKeyboard() {
        this.post {
            this.requestFocus()
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
        }
    }


    private fun retrievePhoneNumberHint() {
        Identity.getSignInClient(requireActivity()).getPhoneNumberHintIntent(request)
            .addOnSuccessListener { result: PendingIntent ->
                try {
                    phoneNumberHintIntentResultLauncher.launch(
                        IntentSenderRequest.Builder(result).build()
                    )
                } catch (e: Exception) {
                    binding.etEnterMob.requestFocus()
                    binding.etEnterMob.showKeyboard()
                }
            }.addOnFailureListener {
                binding.etEnterMob.requestFocus()
                binding.etEnterMob.showKeyboard()
            }
    }

    private fun setupPhoneInput() {
        binding.etEnterMob.apply {
            filters = arrayOf(InputFilter.LengthFilter(10))

            setOnFocusChangeListener { _, hasFocus ->
                binding.phoneInputLayout.setBackgroundResource(
                    if (hasFocus) R.drawable.rounded_homeeditext_clicked
                    else R.drawable.rounded_homeditext_unclicked
                )

                if (hasFocus && !text.isNullOrEmpty()) {
                    showKeyboard()
                }
            }

            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    updateVerifyButtonState(s?.length == 10)
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }


    private fun googleCredential() {
        val credentialManager = CredentialManager.create(requireContext())
        val googleIdOption: GetGoogleIdOption =
            GetGoogleIdOption.Builder().setFilterByAuthorizedAccounts(false)
                .setServerClientId(BuildConfig.GOOGLE_CLIENT_ID).build()
        val request: GetCredentialRequest =
            GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build()

        lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = requireActivity(),
                )
                handleSignIn(result)
            } catch (e: GetCredentialCancellationException) {
                Log.w(TAG, "User cancelled Google sign-in.")
                Toast.makeText(
                    requireContext(), "Sign-in was cancelled. Please try again.", Toast.LENGTH_SHORT
                ).show()
            } catch (e: GetCredentialException) {
                Log.e(TAG, "googleCredential error: ${e.message}")
                Toast.makeText(requireContext(), "Sign-in failed: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error during Google sign-in: ${e.message}")
                Toast.makeText(
                    requireContext(), "Unexpected error: ${e.message}", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    private fun handleSignIn(result: GetCredentialResponse) {
        when (val credential = result.credential) {
            is PublicKeyCredential -> {
                val responseJson = credential.authenticationResponseJson
                Log.e("googleresponswe", responseJson)
            }

            is PasswordCredential -> {
                val username = credential.id
                val password = credential.password
                Log.e("googlerepassword", password)
            }

            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)
                        val idToken = googleIdTokenCredential.idToken

                        Log.e("getIdToken", googleIdTokenCredential.idToken)
                        verifyOtpViewModel.googleAuth(idToken)
                        sharedPreferencesManager.name =
                            googleIdTokenCredential.displayName.toString()
                        sharedPreferencesManager.email = googleIdTokenCredential.id
                        goggleViewModel()
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e(TAG, "Received an invalid google id token response", e)
                    }
                } else {
                    Log.e(TAG, "Unexpected type of credential")
                }
            }

            else -> {
                Log.e(TAG, "Unexpected type of credential")
            }
        }
    }

    private fun goggleViewModel() {
        if (!sharedPreferencesManager.email.isNullOrEmpty()) {
            verifyOtpViewModel.googleAuthResult.observe(viewLifecycleOwner) { result ->
                if (result != null) {
                    sharedPreferencesManager.userId = result.user?.id
                    sharedPreferencesManager.accessToken = result.accessToken
                    sharedPreferencesManager.refreshToken = result.refreshToken

                    userViewModel.fetchUserDetails()

                    userViewModel.userDetails.observe(viewLifecycleOwner) { userDetailsResult ->
                        userDetailsResult.onSuccess { data ->
                            val userDetails = data.getMyDetails

                            if (userDetails.userInformation.address?.city != null && userDetails.userInformation.reference != null && userDetails.userInformation.targetYear != null && userDetails.userInformation.preparingFor != null && userDetails.fullName != null) {
                                navigateToHomeActivity(userDetails.id)
                            } else {
                                sharedPreferencesManager.mobileNo = userDetails.mobileNumber
                                navigateToHome("email")
                            }
                        }.onFailure { exception ->
                            Toast.makeText(
                                requireContext(),
                                "Error fetching details: ${exception.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } else {
                    Log.e("FailureBefore", "${result} ")
                    Toast.makeText(requireContext(), "Invalid OTP", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun retrieveStoredMobileNumber() {
        val storedMobileNumber = sharedPreferencesManager.mobileNo
        if (!storedMobileNumber.isNullOrEmpty()) {
            binding.etEnterMob.setText(storedMobileNumber)
            updateVerifyButtonState(storedMobileNumber.length == 10)
        }
    }

    private fun setupUI() {
        retrieveStoredMobileNumber()
        setupPhoneInput()
        setupTermsAndPrivacyText()
    }

    private fun navigateToHomeActivity(userId: String) {
        sharedPreferencesManager.userId = userId
        Log.e("userIdvero", userId.toString())
        val intent = Intent(requireContext(), HomeActivity::class.java).apply {
            putExtra("userId", userId)
        }
        startActivity(intent)
        requireActivity().finish()
    }

    private fun navigateToHome(loginType: String) {
        val bundle = Bundle().apply {
            putString("loginType", loginType)
        }
        findNavController().navigate(R.id.onboardingFragment, bundle)
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
            val pdfFileName = "TermsandCondition.pdf"
            openPdfFromAssets(pdfFileName)
        }

        override fun updateDrawState(ds: android.text.TextPaint) {
            ds.isUnderlineText = false
        }
    }

    private val privacyClickableSpan = object : ClickableSpan() {
        override fun onClick(widget: View) {
            val pdfFileName = "TermsandCondition.pdf"
            openPdfFromAssets(pdfFileName)
        }

        override fun updateDrawState(ds: android.text.TextPaint) {
            ds.isUnderlineText = false
        }
    }

    private fun openPdfFromAssets(fileName: String) {
        try {
            val inputStream = requireContext().assets.open(fileName)
            val file = File(requireContext().cacheDir, fileName)
            file.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            val uri = FileProvider.getUriForFile(
                requireContext(), "${requireContext().packageName}.provider", file
            )
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, "application/pdf")
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

            if (intent.resolveActivity(requireContext().packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(
                    requireContext(), "No PDF viewer found", Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error opening file", Toast.LENGTH_SHORT).show()
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
            if (result == true) {
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

    override fun onResume() {
        super.onResume()
        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
    }
}
