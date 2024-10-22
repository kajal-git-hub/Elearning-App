package xyz.penpencil.competishun.ui.fragment

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.apollographql.apollo3.api.Optional
import com.google.gson.Gson
import com.student.competishun.gatekeeper.type.UpdateUserInput
import dagger.hilt.android.AndroidEntryPoint
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent.setEventListener
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.data.model.State
import xyz.penpencil.competishun.data.model.StateCity
import xyz.penpencil.competishun.databinding.FragmentOnBoardingBinding
import xyz.penpencil.competishun.ui.main.MainActivity
import xyz.penpencil.competishun.ui.viewmodel.UpdateUserViewModel
import xyz.penpencil.competishun.ui.viewmodel.UserViewModel
import xyz.penpencil.competishun.utils.SharedPreferencesManager
import java.io.IOException
import java.io.InputStream


@AndroidEntryPoint
class OnBoardingFragment : Fragment() {

    private lateinit var binding: FragmentOnBoardingBinding

    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private val updateUserViewModel: UpdateUserViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val handler = Handler(Looper.getMainLooper())
    private var isSelectingState = false
    private var isSelectingCity = false
    private var cityRunnable: Runnable? = null
    private lateinit var stateAdapter: ArrayAdapter<String>
    private lateinit var cityAdapter: ArrayAdapter<String>
    private var runnable: Runnable? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnBoardingBinding.inflate(inflater,container,false)
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

        setEventListener(
            activity!!,
            KeyboardVisibilityEventListener {
                binding.viewToVisible.visibility = View.VISIBLE
            })

        val savedName = sharedPreferencesManager.name
        val savedCity = sharedPreferencesManager.city
        val savedState = sharedPreferencesManager.state
        val phoneNo = sharedPreferencesManager.mobileNo
        val email = sharedPreferencesManager.email
        val statesAndCities = loadStatesAndCities(requireContext())


        val stateList = mutableListOf<String>()
        stateAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, stateList)
        binding.etEnterStateText.setAdapter(stateAdapter)

        val cityList = mutableListOf<String>()
        cityAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, cityList)
        binding.etEnterCityText.setAdapter(cityAdapter)

        binding.etEnterStateText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                if (query.isNotEmpty()) {
                    val filteredStates = statesAndCities?.filter { state ->
                        state.name.contains(
                            query,
                            ignoreCase = true
                        )
                    }
                        ?.map { it.name } ?: emptyList()
                    stateAdapter.clear()
                    stateAdapter.addAll(filteredStates)
                    stateAdapter.notifyDataSetChanged()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })


        binding.etEnterCityText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val selectedState = binding.etEnterStateText.text.toString()
                val selectedStateObj = statesAndCities?.find { it.name == selectedState }
                val query = s.toString().trim()
                if(query.length>=3 && query.length<=5){
                    val filteredCities = selectedStateObj?.cities?.filter { city ->
                        city.contains(
                            query,
                            ignoreCase = true
                        )
                    } ?: emptyList()
                    cityAdapter.clear()
                    cityAdapter.addAll(filteredCities)
                    cityAdapter.notifyDataSetChanged()

                    hideSoftKeyBoard(requireContext(),view)

                }else{
                    val filteredCities = selectedStateObj?.cities?.filter { city ->
                        city.contains(
                            query,
                            ignoreCase = true
                        )
                    } ?: emptyList()
                    cityAdapter.clear()
                    cityAdapter.addAll(filteredCities)
                    cityAdapter.notifyDataSetChanged()
                }

            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.etEnterCityText.setOnItemClickListener { parent, view, position, id ->
            val selectedCity = cityAdapter.getItem(position)

        }



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
        Log.e("getmobl", sharedPreferencesManager.mobileNo.toString())
        Log.e("getEmail", sharedPreferencesManager.email.toString())
        val loginType = arguments?.getString("loginType")
        if (loginType != null && loginType == "email") {
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

        userViewModel.userDetails.observe(requireActivity()) { result ->
            result.onSuccess { data ->
                sharedPreferencesManager.userId = data.getMyDetails.id
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
                updateUserViewModel.updateUser(updateUserInput, null, null)
                val bundle = Bundle().apply {
                    putString("loginType", loginType)
                }
                findNavController().navigate(
                    R.id.action_OnBoardingFragment_to_prepForFragment,
                    bundle
                )
            } else {
                Toast.makeText(context, "Please select a name and city", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun View.hideKeyboard() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }




    fun hideSoftKeyBoard(context: Context, view: View) {
        try {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        } catch (e: Exception) {
            // TODO: handle exception
            e.printStackTrace()
        }

    }
    private fun observeUserDetails() {
        userViewModel.userDetails.observe(viewLifecycleOwner) { result ->
            result.onSuccess { data ->
                Log.d("userDetails", data.getMyDetails.fullName.toString())
                Log.d("userDetails", data.getMyDetails.userInformation.address?.city.toString())
                val name = data.getMyDetails.fullName
                val city = data.getMyDetails.userInformation.address?.city
                val state = data.getMyDetails.userInformation.address?.state
                Log.d("userDState", data.getMyDetails.userInformation.address?.state.toString())
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
                Toast.makeText(
                    requireContext(),
                    "Error fetching details: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
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


    private fun loadJSONFromAsset(context: Context, fileName: String): String? {
        return try {
            val inputStream: InputStream = context.assets.open(fileName)
            val size: Int = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, Charsets.UTF_8)
        } catch (ex: IOException) {
            Log.e("exceptionjson", ex.toString())
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
        Log.e("PhoneNoText", isPhoneValid.toString())
        if (isNameValid && isCityValid && (isPhoneValid || isEmailValid) && isStateValid) {
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
        Log.e("phoneNumberOR email", phone + email)
        return name.length >= 3 && city.length >= 3 && (phone.length >= 10 || isValidEmail(email)) && state.length >= 3
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

        Log.d(
            "OnBoardingFragment email",
            " $email Name, City, phone state: $name, $city, $phone, $state"
        )
    }


    override fun onResume() {
        super.onResume()
        updateNextButtonState()
    }
}
