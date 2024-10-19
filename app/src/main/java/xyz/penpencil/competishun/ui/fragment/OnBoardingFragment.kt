package xyz.penpencil.competishun.ui.fragment

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.apollographql.apollo3.api.Optional
import com.google.gson.Gson
import com.student.competishun.gatekeeper.type.UpdateUserInput
import xyz.penpencil.competishun.ui.viewmodel.UpdateUserViewModel
import xyz.penpencil.competishun.ui.viewmodel.UserViewModel
import xyz.penpencil.competishun.utils.SharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.data.model.State
import xyz.penpencil.competishun.data.model.StateCity
import xyz.penpencil.competishun.databinding.FragmentOnBoardingBinding
import xyz.penpencil.competishun.ui.main.MainActivity
import java.io.IOException
import java.io.InputStream

@AndroidEntryPoint
class OnBoardingFragment : Fragment() {

    private var _binding: FragmentOnBoardingBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private val updateUserViewModel: UpdateUserViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val handler = Handler(Looper.getMainLooper())
    private var isSelectingState = false
    private var isSelectingCity = false
    private var cityRunnable: Runnable? = null
    private var runnable: Runnable? = null
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
        val email = sharedPreferencesManager.email
        val statesAndCities = loadStatesAndCities(requireContext())

        binding.etEnterStateText.setOnClickListener {
            binding.etEnterStateText.setText("")
            hideKeyboard(binding.etEnterStateText)
//            Log.e("statesAndCites",statesAndCities.toString())
//            statesAndCities?.let {
//                val stateNames = it.map { state -> state.name }
//                showStateDropdown(binding.etEnterStateText, stateNames) // Call the updated function
//            }

        }
        binding.etEnterStateText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!isSelectingState) {
                    runnable?.let { handler.removeCallbacks(it) }
                    runnable = Runnable {
                    val query = s.toString()
                    val filteredStates = statesAndCities?.filter { state ->
                        state.name.contains(
                            query,
                            ignoreCase = true
                        ) // Filter states based on input
                    }?.map { it.name } ?: emptyList()

                    // Show the dropdown menu with filtered states
                    if (filteredStates.isNotEmpty()) {
                        showStateDropdown(binding.etEnterStateText, filteredStates)
                    }
                }
                    handler.postDelayed(runnable!!, 300)
                } else {

                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })


        binding.etEnterCityText.setOnClickListener {
            binding.etEnterCityText.setText("")
//            val selectedState = binding.etEnterStateText.text.toString()
//            val selectedStateObj = statesAndCities?.find { it.name == selectedState }
//            Log.e("selectedStateObj",selectedStateObj.toString())
//            selectedStateObj?.let { state ->
//                showCityDropdown(binding.etEnterCityText, state.cities)
//            } ?: run {
//                Toast.makeText(requireContext(), "Please select a state first", Toast.LENGTH_SHORT).show()
//            }
        }


        binding.etEnterCityText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Only filter cities if we are not selecting one
                if (!isSelectingCity) {
                    // Cancel any existing runnable
                    cityRunnable?.let { handler.removeCallbacks(it) }

                    // Create a new runnable for debouncing
                    cityRunnable = Runnable {
                        val query = s.toString().trim()
                        val selectedState = binding.etEnterStateText.text.toString()
                        val selectedStateObj = statesAndCities?.find { it.name == selectedState }

                        // Filter cities based on the selected state and user input
                        val filteredCities = selectedStateObj?.cities?.filter { city ->
                            city.contains(query, ignoreCase = true) // Filter cities based on input
                        } ?: emptyList()

                        // Show the dropdown menu with filtered cities
                        if (filteredCities.isNotEmpty()) {
                            showCityDropdown(binding.etEnterCityText, filteredCities)
                        }
                    }
                    // Post the runnable with a delay (e.g., 300 milliseconds)
                    handler.postDelayed(cityRunnable!!, 300)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })


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
        email?.let {
            binding.etEnterEmailText.setText(it)
        }

        //condition need to check for google
        Log.e("getmobl",sharedPreferencesManager.mobileNo.toString())
        Log.e("getEmail",sharedPreferencesManager.email.toString())
        val loginType = arguments?.getString("loginType")
        if (loginType != null && loginType == "email"){
            binding.clEnterNo.visibility = View.VISIBLE
            binding.etPhoneNoText.visibility = View.VISIBLE
            binding.etEmailText.visibility = View.GONE
            binding.etEnterEmailText.visibility = View.GONE
        }else {
            binding.clEnterNo.visibility = View.GONE
            binding.etPhoneNoText.visibility = View.GONE
            binding.etEmailText.visibility = View.VISIBLE
            binding.etEnterEmailText.visibility = View.VISIBLE
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
                val bundle = Bundle().apply {
                    putString("loginType", loginType)
                }
                findNavController().navigate(R.id.action_OnBoardingFragment_to_prepForFragment,bundle)
            } else {
                Toast.makeText(context, "Please select a name and city", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showCityDropdown(etEnterCityText: EditText, cities: List<String>) {
        val popupMenu = PopupMenu(requireContext(), etEnterCityText, Gravity.NO_GRAVITY, 0, xyz.penpencil.competishun.R.style.CustomPopupMenu)
        popupMenu.menu.clear()

        // Add cities to the popup menu
        for (city in cities) {
            popupMenu.menu.add(city)
        }

        // Set the selected city to the EditText
        popupMenu.setOnMenuItemClickListener { item ->
            isSelectingCity = true
            val selectedCity: String = item.title.toString()
            etEnterCityText.setText(selectedCity)
            hideKeyboard(etEnterCityText)
            isSelectingCity = false
            true
        }

        popupMenu.show()
    }


    private fun hideKeyboard(view: View) {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun observeUserDetails() {
        userViewModel.userDetails.observe(viewLifecycleOwner) { result ->
            result.onSuccess { data ->
                Log.d("userDetails",data.getMyDetails.fullName.toString())
                Log.d("userDetails",data.getMyDetails.userInformation.address?.city.toString())
                val name = data.getMyDetails.fullName
                val city = data.getMyDetails.userInformation.address?.city
                val state = data.getMyDetails.userInformation.address?.state
                Log.d("userDState",data.getMyDetails.userInformation.address?.state.toString())
//                if (!name.isNullOrEmpty()) {
//                    binding.etEnterHereText.setText(name)
//                }
                if (!city.isNullOrEmpty()) {
                    binding.etEnterCityText.setText(city)
                }

                if (!state.isNullOrEmpty()) {
                    binding.etEnterStateText.setText(state)
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
        binding.etEnterEmailText.addTextChangedListener(textWatcher)
        binding.etEnterStateText.addTextChangedListener(textWatcher)
    }

    private fun loadStatesAndCities(context: Context): List<State>? {
        val jsonString = loadJSONFromAsset(context, "states_cities.json")
        return if (!jsonString.isNullOrEmpty()) {
            val gson = Gson()
            val stateCity = gson.fromJson(jsonString, StateCity::class.java)
            stateCity.states
        } else {
            null
        }
    }

    private fun showStateDropdown(editText: EditText, states: List<String>) {
        val popupMenu = PopupMenu(requireContext(), editText, Gravity.NO_GRAVITY, 0, xyz.penpencil.competishun.R.style.CustomPopupMenu)
        // Clear previous menu items
        popupMenu.menu.clear()

        // Add states to the popup menu
        for (state in states) {
            popupMenu.menu.add(state)
        }

        // Set the selected state to the EditText and reset the city field
        popupMenu.setOnMenuItemClickListener { item ->
            isSelectingState = true
            val selectedState: String = item.title.toString()
            editText.setText(selectedState)
            binding.etEnterCityText.text = null
            hideKeyboard(editText)
            isSelectingState = false
            editText.clearFocus()
            // Reset city field when state is changed
            true
        }

        popupMenu.show()
    }




    private fun loadJSONFromAsset(context: Context, fileName: String): String? {
        return try {
            val inputStream: InputStream = context.assets.open(fileName)
            val size: Int = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, Charsets.UTF_8)
        } catch (ex: IOException) {
            Log.e("exceptionjson",ex.toString())
            ex.printStackTrace()
            null
        }
    }


    private fun updateNextButtonState() {
        val isNameValid = binding.etEnterHereText.text.toString().trim().length >= 3
        val isCityValid = binding.etEnterCityText.text.toString().trim().length >= 3
        val isPhoneValid = binding.etEnterMob.text.toString().trim().length >= 10
        val isEmailValid = isValidEmail(binding.etEnterEmailText.text.toString().trim())
        val isStateValid = binding.etEnterStateText.text.toString().trim().length >= 3
        Log.e("PhoneNoText",isPhoneValid.toString())
        if (isNameValid && isCityValid && (isPhoneValid || isEmailValid)  && isStateValid) {
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
        val email = binding.etEnterEmailText.text.toString().trim()
        Log.e("phoneNumberOR email",phone + email)
        return name.length >= 3 && city.length >= 3 &&( phone.length >= 10 || isValidEmail(email)) && state.length >= 3
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        return email.trim().matches(emailPattern.toRegex())
    }

    private fun saveNameAndCity() {
        val name = binding.etEnterHereText.text.toString().trim()
        val city = binding.etEnterCityText.text.toString().trim()
        val state = binding.etEnterStateText.text.toString().trim()
        val phone = binding.etEnterMob.text.toString().trim()
        val email = binding.etEnterEmailText.text.toString().trim()
        sharedPreferencesManager.name = name
        sharedPreferencesManager.city = city
        sharedPreferencesManager.state = state
        sharedPreferencesManager.mobileNo = phone
        sharedPreferencesManager.email = email

//        userViewModel.updateUserDetails(name, city)

        Log.d("OnBoardingFragment email", " $email Name, City, phone state: $name, $city, $phone, $state")
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
