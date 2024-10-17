package xyz.penpencil.competishun.ui.fragment

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
import com.apollographql.apollo3.api.Optional
import com.student.competishun.gatekeeper.type.UpdateUserInput
import xyz.penpencil.competishun.ui.viewmodel.UpdateUserViewModel
import xyz.penpencil.competishun.ui.viewmodel.UserViewModel
import xyz.penpencil.competishun.utils.SharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.databinding.FragmentOnBoardingBinding
import xyz.penpencil.competishun.ui.main.MainActivity

@AndroidEntryPoint
class OnBoardingFragment : Fragment() {

    private var _binding: FragmentOnBoardingBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private val updateUserViewModel: UpdateUserViewModel by viewModels()
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

        val savedName = sharedPreferencesManager.name
        val savedCity = sharedPreferencesManager.city
        val savedState = sharedPreferencesManager.state
        val phoneNo = sharedPreferencesManager.mobileNo
        savedName?.let {
            binding.etEnterHereText.setText(it)
        }
        savedCity?.let {
            binding.etEnterCityText.setText(it)
        }
        savedState?.let {
            binding.etEnterStateText.setText(it)
        }
        phoneNo?.let {
            binding.etEnterMob.setText(it)
        }

        //condition need to check for google
        if (sharedPreferencesManager.mobileNo.isNullOrEmpty()){
            binding.clEnterNo.visibility = View.VISIBLE
            binding.etPhoneNoText.visibility = View.VISIBLE
        }

        userViewModel.userDetails.observe(requireActivity()) { result ->
            result.onSuccess { data ->
               sharedPreferencesManager.userId=data.getMyDetails.id
            }.onFailure { exception ->
                Log.e("mainActivity details", exception.message.toString())
            }
        }

        observeUserDetails()
        userViewModel.fetchUserDetails()

        setupTextWatchers()

        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_OnBoardingFragment_to_loginFragment)
        }

        binding.NextOnBoarding.setOnClickListener {
            if (isCurrentStepValid()) {
                saveNameAndCity()
                val updateUserInput = UpdateUserInput(
                    city = Optional.Present(sharedPreferencesManager.city),
                    state = Optional.present(sharedPreferencesManager.state),
                    fullName = Optional.Present(sharedPreferencesManager.name),
                    )
                updateUserViewModel.updateUser(updateUserInput,null,null)

                findNavController().navigate(R.id.action_OnBoardingFragment_to_prepForFragment)
            } else {
                Toast.makeText(context, "Please select a name and city", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeUserDetails() {
        userViewModel.userDetails.observe(viewLifecycleOwner) { result ->
            result.onSuccess { data ->
                Log.d("userDetails",data.getMyDetails.fullName.toString())
                Log.d("userDetails",data.getMyDetails.userInformation.address?.city.toString())
                val name = data.getMyDetails.fullName
                val city = data.getMyDetails.userInformation.address?.city
                val state = data.getMyDetails.userInformation.address?.state

//                if (!name.isNullOrEmpty()) {
//                    binding.etEnterHereText.setText(name)
//                }
                if (!city.isNullOrEmpty()) {
                    binding.etEnterCityText.setText(city)
                }

                if (!state.isNullOrEmpty()) {
                    binding.etEnterCityText.setText(city)
                }

                sharedPreferencesManager.name = name
                sharedPreferencesManager.city = city
                sharedPreferencesManager.state = state

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
        binding.etEnterMob.addTextChangedListener(textWatcher)
        binding.etEnterStateText.addTextChangedListener(textWatcher)
    }

    private fun updateNextButtonState() {
        val isNameValid = binding.etEnterHereText.text.toString().trim().length >= 3
        val isCityValid = binding.etEnterCityText.text.toString().trim().length >= 3
        val isPhoneValid = binding.etEnterMob.text.toString().trim().length >= 10
        val isStateValid = binding.etEnterStateText.text.toString().trim().length >= 10
        Log.e("PhoneNoText",isPhoneValid.toString())
        if (isNameValid && isCityValid && isPhoneValid && isStateValid) {
            binding.NextOnBoarding.setBackgroundResource(R.drawable.second_getstarteddone)
        } else {
            binding.NextOnBoarding.setBackgroundResource(R.drawable.second_getstarted)
        }
    }

    private fun isCurrentStepValid(): Boolean {
        val name = binding.etEnterHereText.text.toString().trim()
        val city = binding.etEnterCityText.text.toString().trim()
        val phone = binding.etEnterMob.text.toString().trim()
        val state = binding.etEnterStateText.text.toString().trim()
        Log.e("phoneNumbertext",phone)
        return name.length >= 3 && city.length >= 3 && phone.length >= 10 && state.length >= 3
    }

    private fun saveNameAndCity() {
        val name = binding.etEnterHereText.text.toString().trim()
        val city = binding.etEnterCityText.text.toString().trim()
        val state = binding.etEnterStateText.text.toString().trim()
        val phone = binding.etEnterMob.text.toString().trim()
        sharedPreferencesManager.name = name
        sharedPreferencesManager.city = city
        sharedPreferencesManager.state = state
        sharedPreferencesManager.mobileNo = phone

//        userViewModel.updateUserDetails(name, city)

        Log.d("OnBoardingFragment", "Name, City, phone state: $name, $city, $phone, $state")
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
