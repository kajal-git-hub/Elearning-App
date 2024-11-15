package xyz.penpencil.competishun.ui.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.apollographql.apollo3.api.Optional
import com.google.gson.Gson
import com.student.competishun.gatekeeper.type.UpdateUserInput
import dagger.hilt.android.AndroidEntryPoint
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.data.api.ApiProcess
import xyz.penpencil.competishun.data.model.State
import xyz.penpencil.competishun.data.model.StateCity
import xyz.penpencil.competishun.data.model.UpdateUserResponse
import xyz.penpencil.competishun.databinding.FragmentOnBoardingBinding
import xyz.penpencil.competishun.ui.main.MainActivity
import xyz.penpencil.competishun.ui.viewmodel.UpdateUserViewModel
import xyz.penpencil.competishun.ui.viewmodel.UserViewModel
import xyz.penpencil.competishun.utils.SharedPreferencesManager
import java.io.IOException

@AndroidEntryPoint
class OnBoardingFragment : Fragment() {

    private lateinit var binding: FragmentOnBoardingBinding
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private val updateUserViewModel: UpdateUserViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private lateinit var stateAdapter: ArrayAdapter<String>
    private lateinit var cityAdapter: ArrayAdapter<String>

    private var loginType : String? = null
    val stateCity: List<State> by lazy { loadStatesAndCities()?: emptyList() }
    private var selectedState: String? = null
    private var selectedCity: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnBoardingBinding.inflate(inflater, container, false)
        sharedPreferencesManager = (requireActivity() as MainActivity).sharedPreferencesManager
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigate(R.id.loginFragment)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginType = if(sharedPreferencesManager.loginType!=null){
            sharedPreferencesManager.loginType
        }else{
            arguments?.getString("loginType")

        }
        setupStateAndCityAdapters()
        setupUIWithSavedData()
        setupTextWatchers(loginType)
        observeUserDetails()
        handleLoginTypeVisibility(loginType)
        userViewModel.fetchUserDetails()

        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_OnBoardingFragment_to_loginFragment)
            sharedPreferencesManager.clearUserData()
        }

        binding.NextOnBoarding.setOnClickListener {
            if (isCurrentStepValid(loginType)) {
                sharedPreferencesManager.loginType  = loginType
                saveUserDetails()
                updateUser(loginType)
            } else {
                Toast.makeText(context, "Please fill all the details", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupStateAndCityAdapters() {
        stateAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, mutableListOf())
        cityAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, mutableListOf())

        binding.etEnterStateText.setAdapter(stateAdapter)
        binding.etEnterCityText.setAdapter(cityAdapter)

/*        binding.etEnterStateText.addTextChangedListener { query ->
            if (!query.isNullOrEmpty()) {
                val filteredStates = loadStatesAndCities(requireContext())?.filter {
                    it.name.contains(query, ignoreCase = true)
                }?.map { it.name } ?: emptyList()
                stateAdapter.clear()
                stateAdapter.addAll(filteredStates)
                stateAdapter.notifyDataSetChanged()
            }
        }

        binding.etEnterCityText.addTextChangedListener { query ->
            if (!query.isNullOrEmpty() && query.length in 3..5) {
                val selectedState = binding.etEnterStateText.text.toString()
                val cities = loadStatesAndCities(requireContext())?.find {
                    it.name == selectedState
                }?.cities ?: emptyList()

                val filteredCities = cities.filter {
                    it.contains(query, ignoreCase = true)
                }
                cityAdapter.clear()
                cityAdapter.addAll(filteredCities)
                cityAdapter.notifyDataSetChanged()

                hideSoftKeyBoard(requireContext(), view = view)
            }else{
                val selectedState = binding.etEnterStateText.text.toString()
                val cities = loadStatesAndCities(requireContext())?.find {
                    it.name == selectedState
                }?.cities ?: emptyList()

                val filteredCities = cities.filter {
                    it.contains(query.toString(), ignoreCase = true)
                }
                cityAdapter.clear()
                cityAdapter.addAll(filteredCities)
                cityAdapter.notifyDataSetChanged()
            }
        }*/

        val stateAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, stateCity.map { it.name })
        binding.etEnterStateText.setAdapter(stateAdapter)

        binding.etEnterStateText.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position) as String
            val selectedIndex = stateCity.indexOfFirst { it.name == selectedItem }

            if (selectedIndex != -1) {
                selectedState = selectedItem // Save the selected state
                val data = stateCity[selectedIndex].cities
                val cityAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, data)
                binding.etEnterCityText.setAdapter(cityAdapter)
                binding.etEnterCityText.text = null // Clear the city text input
                selectedCity = null // Clear the previously selected city when a new state is chosen
            } else {
                binding.etEnterStateText.text = null // Clear invalid input
                selectedState = null
                Toast.makeText(requireContext(), "Please select a valid state", Toast.LENGTH_SHORT).show()
            }
        }

        binding.etEnterCityText.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position) as String
            val currentState = selectedState?.let { state ->
                stateCity.find { it.name == state }
            }

            if (currentState?.cities?.contains(selectedItem) == true) {
                selectedCity = selectedItem // Save the selected city
                Log.e("Selected City", "Selected City: $selectedCity")
            } else {
                binding.etEnterCityText.text = null // Clear invalid input
                selectedCity = null
                Toast.makeText(requireContext(), "Please select a valid city", Toast.LENGTH_SHORT).show()
            }
        }

        binding.etEnterStateText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val stateName = binding.etEnterStateText.text.toString()
                if (stateCity.none { it.name == stateName }) {
                    binding.etEnterStateText.text = null
                    selectedState = null
                    Toast.makeText(requireContext(), "Please select a valid state", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.etEnterCityText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val cityName = binding.etEnterCityText.text.toString()
                val validCities = selectedState?.let { state ->
                    stateCity.find { it.name == state }?.cities
                }

                if (validCities == null || !validCities.contains(cityName)) {
                    binding.etEnterCityText.text = null
                    selectedCity = null
                    Toast.makeText(requireContext(), "Please select a valid city", Toast.LENGTH_SHORT).show()
                }
            }
        }


        binding.etEnterCityText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.etEnterCityText.post {
                    binding.etEnterCityText.showDropDown()
                }
            }
        }
    }


    private fun setupUIWithSavedData() {
        with(sharedPreferencesManager) {
            binding.etEnterHereText.setText(name)
            binding.etEnterCityText.setText(city)
            binding.etEnterStateText.setText(state)
            binding.etEnterMob.setText(mobileNo)
            binding.etEnterEmailText.setText(email)
        }
    }

    private fun setupTextWatchers(loginType: String?) {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateNextButtonState(loginType)
            }
        }
        binding.etEnterHereText.addTextChangedListener(textWatcher)
        binding.etEnterCityText.addTextChangedListener(textWatcher)
        binding.etEnterMob.addTextChangedListener(textWatcher)
        binding.etEnterEmailText.addTextChangedListener(textWatcher)
        binding.etEnterStateText.addTextChangedListener(textWatcher)
    }

    private fun loadStatesAndCities(): List<State>? {
        return try {
            val jsonString = requireActivity().assets.open("states_cities.json").use { inputStream ->
                inputStream.readBytes().toString(Charsets.UTF_8)
            }
            Gson().fromJson(jsonString, StateCity::class.java).states
        } catch (ex: IOException) {
            Log.e("OnBoardingFragment", "Error loading JSON", ex)
            null
        }
    }

    private fun handleLoginTypeVisibility(loginType: String?) {
        if (loginType == "email") {
            binding.clEnterNo.visibility = View.VISIBLE
            binding.etPhoneNoText.visibility = View.VISIBLE
            binding.etEmailText.visibility = View.GONE
            binding.etEnterEmailText.visibility = View.GONE
        } else {
            binding.clEnterNo.visibility = View.GONE
            binding.etPhoneNoText.visibility = View.GONE
            binding.etEmailText.visibility = View.VISIBLE
            binding.etEnterEmailText.visibility = View.VISIBLE
        }
    }

    private fun updateNextButtonState(loginType: String?) {
        val isNameValid = binding.etEnterHereText.text.toString().trim().length >= 3
        val isCityValid = binding.etEnterCityText.text.toString().trim().length >= 3
        val isStateValid = binding.etEnterStateText.text.toString().trim().length >= 3
        val isPhoneValid = if (loginType == "email") {
            binding.etEnterMob.text.toString().trim().length == 10
        } else {
            true
        }
        val isEmailValid = if (loginType != "email") {
            isEmailValid(binding.etEnterEmailText.text.toString().trim())
        } else {
            true
        }


        binding.NextOnBoarding.isEnabled = isNameValid && isCityValid && isStateValid && (isPhoneValid || isEmailValid)
        binding.NextOnBoarding.setBackgroundResource(
            if (binding.NextOnBoarding.isEnabled) {
                R.drawable.second_getstarteddone
            } else {
                R.drawable.second_getstarted
            }
        )
    }

    private fun isEmailValid(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"
        return email.matches(emailPattern.toRegex()) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isCurrentStepValid(loginType: String?): Boolean {
        val name = binding.etEnterHereText.text.toString().trim()
        val city = binding.etEnterCityText.text.toString().trim()
        val phone = binding.etEnterMob.text.toString().trim()
        val state = binding.etEnterStateText.text.toString().trim()
        val email = binding.etEnterEmailText.text.toString().trim()


        val isNameValid = name.length >= 3
        val isCityValid = city.length >= 3
        val isStateValid = state.length >= 3
        val isPhoneValid = if (loginType == "email") phone.length == 10 else true
        val isEmailValid = if (loginType != "email") isEmailValid(email) else true


        return when {
            !isNameValid || !isCityValid || !isStateValid -> {
                Toast.makeText(context, "Please fill all the details", Toast.LENGTH_SHORT).show()
                false
            }
            loginType == "email" && !isPhoneValid -> {
                Toast.makeText(context, "Please enter a valid phone number", Toast.LENGTH_SHORT).show()
                false
            }
            loginType != "email" && !isEmailValid -> {
                Toast.makeText(context, "Please fill a valid email", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }

    private fun saveUserDetails() {
        sharedPreferencesManager.apply {
            name = binding.etEnterHereText.text.toString().trim()
            city = binding.etEnterCityText.text.toString().trim()
            state = binding.etEnterStateText.text.toString().trim()
            mobileNo = binding.etEnterMob.text.toString().trim()
            email = binding.etEnterEmailText.text.toString().trim()
        }
    }

    private fun updateUser(loginType: String?) {
        Log.d("updateUserMob: ",sharedPreferencesManager.mobileNo.toString())
        val updateUserInput = UpdateUserInput(
            city = Optional.Present(sharedPreferencesManager.city),
            state = Optional.Present(sharedPreferencesManager.state),
            fullName = Optional.Present(sharedPreferencesManager.name),
            mobileNumber = if(loginType=="email")  Optional.Present(sharedPreferencesManager.mobileNo)else Optional.Absent,
            email = Optional.Present(sharedPreferencesManager.email)
        )

        updateUserViewModel.updateUserErrorHandled(updateUserInput, null, null)

        updateUserViewModel.updateUserErrorHandledResult.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is ApiProcess.Success -> {
                    navigateToNextFragment(loginType)
                }
                is ApiProcess.Failure -> {
                    Log.e("gettingUserUpdateFail", "Error: ${result.message}")
                    Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                }
                ApiProcess.Loading -> {
                    Log.d("UpdateUser", "Loading user update...")
                }
                else -> {
                    Log.e("gettingUserUpdateFail", "Unexpected result")
                }
            }
        })
    }


    //    private fun observeUserDetails() {
//        userViewModel.userDetails.observe(viewLifecycleOwner) { user ->
//            if (user != null) {
//                binding.etEnterHereText.setText(user.fullName)
//                binding.etEnterCityText.setText(user.city)
//                binding.etEnterStateText.setText(user.state)
//                binding.etEnterMob.setText(user.mobile)
//                binding.etEnterEmailText.setText(user.email)
//            }
//        }
//    }
    private fun observeUserDetails() {
        userViewModel.userDetails.observe(viewLifecycleOwner) { result ->
            result.onSuccess { data ->
                sharedPreferencesManager.apply {
                    userId = data.getMyDetails.id
                    name = data.getMyDetails.fullName
                    city = data.getMyDetails.userInformation.address?.city
                    state = data.getMyDetails.userInformation.address?.state
                    mobileNo = data.getMyDetails.mobileNumber
                    email = data.getMyDetails.email
                }
            }.onFailure {
                Log.e("OnBoardingFragment", "Error observing user details", it)
            }
        }
    }

    private fun navigateToNextFragment(loginType: String?) {
        if (loginType == "email") {
            findNavController().navigate(R.id.PrepForFragment)
        } else {
            findNavController().navigate(R.id.PrepForFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        updateNextButtonState(loginType)
        handleLoginTypeVisibility(loginType)
    }
}
