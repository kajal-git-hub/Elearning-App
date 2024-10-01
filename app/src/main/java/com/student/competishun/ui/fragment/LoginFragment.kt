package com.student.competishun.ui.fragment

import android.content.Intent
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
import androidx.activity.result.ActivityResultLauncher
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.PasswordCredential
import androidx.credentials.PublicKeyCredential
import androidx.credentials.exceptions.GetCredentialException
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.student.competishun.R
import com.student.competishun.databinding.FragmentLoginBinding
import com.student.competishun.ui.main.HomeActivity
import com.student.competishun.ui.main.MainActivity
import com.student.competishun.ui.viewmodel.GetOtpViewModel
import com.student.competishun.ui.viewmodel.UserViewModel
import com.student.competishun.ui.viewmodel.VerifyOtpViewModel
import com.student.competishun.utils.SharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.UUID


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
    private val RC_SIGN_IN = 1001
    private val userViewModel: UserViewModel by viewModels()
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var signInLauncher: ActivityResultLauncher<Intent>

    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        sharedPreferencesManager = (requireActivity() as MainActivity).sharedPreferencesManager
        firebaseAuth = FirebaseAuth.getInstance()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.client_id))
//            .requestEmail()
//            .requestProfile()
//            .build()
//        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)


        setupUI()
        setupObservers()

//

        binding.btnVerify.setOnClickListener {
            handleVerifyButtonClick()
        }
        val account = GoogleSignIn.getLastSignedInAccount(requireContext())
        if (account != null) {
            Log.d("accountfount", "User is already signed in: ${account.email}")
        } else {
            Log.d("account", "No account signed in.")
        }

        binding.btnGoogleLogin.setOnClickListener {
                Log.d("GoogleSignIn", "Signed out successfully, triggering new sign-in.")
                googleCredential() // Trigger Google Sign-In
         }

        // Set up sign-in button listener
//        binding.btnGoogleLogin.setOnClickListener {
//                Log.d("GoogleSignIn", "Signed out successfully, triggering new sign-in.")
//                googleCredential() // Trigger Google Sign-In
//        }
    }
    fun googleCredential() {
        val credentialManager = CredentialManager.create(requireContext())
        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        val hashCode = digest.joinToString("") { "%02x".format(it) }

       val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId("887693153546-mv6cfeppj49al2c2bdpainrh6begq6bi.apps.googleusercontent.com")
         //   .setNonce(hashCode)
            .build()
        Log.e("gettingHashCode",hashCode)

        val request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = requireActivity(),
                )
                handleSignIn(result)
            } catch (e: GetCredentialException) {
                // Handle the case when user cancels the sign-in process or another error occurs
                Log.e("GoogleCredentialError", e.localizedMessage.toString() + e.message)
                if (e.message?.contains("cancelled by the user") == true) {
                    Log.e("GoogleUserError", "Sign-in cancelled by the user")
                    Toast.makeText(requireContext(), "Sign-in cancelled. Please try again.", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("GoogleException", "Sign-in failed: ${e.message}")
                    Toast.makeText(requireContext(), "Sign-in failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                // Handle unexpected errors
                Log.e("GoogleSignInError", "Unexpected error: ${e.message}")
                Toast.makeText(requireContext(), "Unexpected error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

    }

    fun handleSignIn(result: GetCredentialResponse) {
        // Handle the successfully returned credential.
        val credential = result.credential

        when (credential) {

            // Passkey credential
            is PublicKeyCredential -> {
                // Share responseJson such as a GetCredentialResponse on your server to
                // validate and authenticate
                val responseJson = credential.authenticationResponseJson
                Log.e("googleresponswe",responseJson)
            }

            // Password credential
            is PasswordCredential -> {
                // Send ID and password to your server to validate and authenticate.
                val username = credential.id
                val password = credential.password
                Log.e("googlerepassword",password)
            }

            // GoogleIdToken credential
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        // Use googleIdTokenCredential and extract the ID to validate and
                        // authenticate on your server.
                        val googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(credential.data)
                        val idToken = googleIdTokenCredential.idToken

                        Log.e("getIdToken",googleIdTokenCredential.idToken)
                        verifyOtpViewModel.googleAuth(idToken)
                        sharedPreferencesManager.name = googleIdTokenCredential.displayName.toString()
                        sharedPreferencesManager.email = googleIdTokenCredential.id
                        goggleViewModel()
                        Log.e("getIdid",googleIdTokenCredential.id)
                        Log.e("getIdUser",googleIdTokenCredential.displayName.toString())
                        Log.e("getIdno",googleIdTokenCredential.phoneNumber.toString())
                        // You can use the members of googleIdTokenCredential directly for UX
                        // purposes, but don't use them to store or control access to user
                        // data. For that you first need to validate the token:
                        // pass googleIdTokenCredential.getIdToken() to the backend server.
//                        GoogleIdTokenVerifier verifier =
//                        GoogleIdToken idToken = verifier.verify(idTokenString);
                        // To get a stable account identifier (e.g. for storing user data),
                        // use the subject ID:
                      //  idToken.getPayload().getSubject()
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e(TAG, "Received an invalid google id token response", e)
                    }
                } else {
                    // Catch any unrecognized custom credential type here.
                    Log.e(TAG, "Unexpected type of credential")
                }
            }

            else -> {
                // Catch any unrecognized credential type here.
                Log.e(TAG, "Unexpected type of credential")
            }
        }
    }

    private fun goggleViewModel() {
        Log.d("sharednumber", sharedPreferencesManager.mobileNo.toString())
        if (!sharedPreferencesManager.email.isNullOrEmpty())

        verifyOtpViewModel.googleAuthResult.observe(viewLifecycleOwner) { result ->
            if (result != null) {
                Log.e(
                    "Success in Verify",
                    "${result.user} ${result.refreshToken} ${result.accessToken}"
                )
                sharedPreferencesManager.userId = result.user?.id
                sharedPreferencesManager.accessToken = result.accessToken
                sharedPreferencesManager.refreshToken = result.refreshToken

                userViewModel.fetchUserDetails()

                // Observe user details
                userViewModel.userDetails.observe(viewLifecycleOwner) { userDetailsResult ->
                    userDetailsResult.onSuccess { data ->
                        val userDetails = data.getMyDetails

                        if (userDetails.userInformation.address?.city!=null && userDetails.userInformation.reference!=null && userDetails.userInformation.targetYear!=null && userDetails.userInformation.preparingFor!=null && userDetails.fullName!=null) {

                            navigateToHomeActivity(userDetails.id)
                        }
//                        else if (userDetails.userInformation.fatherName!=null) {
//                            navigateToMyCourse()
//                        }
                        else {
                            // Store necessary data in SharedPreferencesManager
                            sharedPreferencesManager.mobileNo = userDetails.mobileNumber
                            navigateToHome()
                        }
                    }.onFailure { exception ->
                        Log.e("mainActivitydetails", exception.message.toString())
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

    private fun navigateToHomeActivity(userId:String) {
        sharedPreferencesManager.userId = userId
        Log.e("userIdvero",userId.toString())
        val intent = Intent(requireContext(), HomeActivity::class.java).apply {
            putExtra("userId", userId)
        }
        startActivity(intent)
        requireActivity().finish()
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        Log.e("insidehanldes", completedTask.toString())
        try {
            val account = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                // Google Sign-In was successful, authenticate with Firebase
                firebaseAuthWithGoogle(account)
            }else {
                Log.e("GoogleSignAccount", "Google sign-in account is null")
            }
        } catch (e: ApiException) {
            // Google Sign-In failed, display a message
            Toast.makeText(requireContext(), "Google sign-in failed", Toast.LENGTH_SHORT).show()
            Log.e("GoogleSignIn", "Google sign-in failed", e)
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, navigate to profile
                    navigateToHome()
                    Toast.makeText(requireContext(), "Firebase authentication successful", Toast.LENGTH_SHORT).show()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(requireContext(), "Firebase authentication failed", Toast.LENGTH_SHORT).show()
                    Log.e("FirebaseAuth", "Firebase authentication failed", task.exception)
                }
            }
    }

    private fun navigateToHome() {
        findNavController().navigate(R.id.onboardingFragment)
    }

    private fun createAccountWithGoogle(account: GoogleSignInAccount) {
        val googleId = account.id
        val googleEmail = account.email
        val googleToken = account.idToken // This token is used for backend authentication
        // Send these details to your backend server to create a user account
        // Proceed with the account creation flow
        Log.e("GoogleSignIn", "Google ID: $googleId, Email: $googleEmail, Token: $googleToken")
    }


    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        signInLauncher.launch(signInIntent)
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
