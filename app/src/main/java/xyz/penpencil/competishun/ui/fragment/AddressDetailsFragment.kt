package xyz.penpencil.competishun.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.apollographql.apollo3.api.Optional
import dagger.hilt.android.AndroidEntryPoint
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.data.api.ApiProcess
import xyz.penpencil.competishun.data.model.UpdateUserResponse
import xyz.penpencil.competishun.databinding.FragmentAddressDetailsBinding
import xyz.penpencil.competishun.ui.main.HomeActivity
import xyz.penpencil.competishun.ui.viewmodel.UpdateUserViewModel
import xyz.penpencil.competishun.utils.SharedPreferencesManager

@AndroidEntryPoint
class AddressDetailsFragment : DrawerVisibility() {

    private var _binding: FragmentAddressDetailsBinding?=null
    private val binding get() = _binding!!
    lateinit var sharedPreferencesManager: SharedPreferencesManager
    private val updateUserViewModel: UpdateUserViewModel by viewModels()

    private var fieldsToVisible = arrayOf<String>()
    private var courseId: String = ""

    private var flatAddress = ""
    private var pinCode = ""
    private var city = ""
    private var state = ""
    private var cityToEnter = ""
    private var stateToEnter = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddressDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferencesManager = SharedPreferencesManager(requireContext())

        (activity as? HomeActivity)?.showBottomNavigationView(false)
        (activity as? HomeActivity)?.showFloatingButton(false)
        arguments?.let {
            fieldsToVisible = it.getStringArray("FIELD_REQUIRED") ?: emptyArray()
            courseId = it.getString("IDS", "")
        }

        binding.etState.setText(sharedPreferencesManager.state)
        binding.etCity.setText(sharedPreferencesManager.city)

        setupCharacterCounter()
        pinCodeCheck()

        binding.etBTUpload.setOnClickListener {
            findNavController().navigate(R.id.AdditionalDetailsFragment)
        }

        binding.btnAddressDetails.setOnClickListener {
            if(isAddressFormValid()){
                sharedPreferencesManager.putString("current$courseId", "")
                sharedPreferencesManager.putBoolean(courseId, true)
                findNavController().navigate(R.id.action_AddressDetail_to_CourseEmpty)
                cityToEnter = city.ifEmpty {
                    sharedPreferencesManager.city.toString()
                }

                stateToEnter = state.ifEmpty {
                    sharedPreferencesManager.state.toString()
                }

                val updateUserInput = com.student.competishun.gatekeeper.type.UpdateUserInput(
                    pinCode = Optional.present(pinCode),
                    addressLine1 = Optional.present(flatAddress),
                    city = Optional.present(cityToEnter),
                    state = Optional.present(stateToEnter)
                )
                updateUserViewModel.updateUser(updateUserInput, null, null)
            }else {
                Toast.makeText(requireContext(), "Please fill all fields.", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        updateUserViewModel.updateUserErrorHandledResult.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is ApiProcess.Success -> {
                    val data: UpdateUserResponse = result.data
                    Log.d("Address",result.data.user?.userInformation?.addressLine1.toString())


                }
                is ApiProcess.Failure -> {
                    Log.e("gettingUserUpdatefail", "Error: ${result.message}")
                    Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                }
                ApiProcess.Loading -> {
                    Log.d("UpdateUser", "Loading user update...")
                }
                else -> {
                    Log.e("gettingUserUpdatefail", "Unexpected result")
                }
            }
        })





        binding.etContentAddress.addTextChangedListener(textWatcher)
        binding.etPincode.addTextChangedListener(textWatcher)
        binding.etCity.addTextChangedListener(textWatcher)
        binding.etState.addTextChangedListener(textWatcher)
    }
    private fun isAddressFormValid(): Boolean {
         flatAddress = binding.etContentAddress.text.toString()
         pinCode = binding.etPincode.text.toString()
         city = binding.etCity.text.toString()
         state = binding.etState.text.toString()

        return flatAddress.isNotEmpty() && pinCode.isNotEmpty() && city.isNotEmpty() && state.isNotEmpty()
    }
    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            updateButtonState()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }


    private fun updateButtonState() {
        if (isAddressFormValid()) {
            binding.btnAddressDetails.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.blue_3E3EF7
                )
            )
            binding.btnAddressDetails.isEnabled = true
        } else {
            binding.btnAddressDetails.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.gray_border
                )
            )
            binding.btnAddressDetails.isEnabled = false
        }
    }

    private fun pinCodeCheck(){
        binding.etPincode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val pincode = s?.toString() ?: ""
                if (pincode.length > 6) {
                    Toast.makeText(requireContext(), "Pincode cannot be more than 6 digits.", Toast.LENGTH_SHORT).show()
                    val trimmedPincode = pincode.substring(0, 6)
                    binding.etPincode.setText(trimmedPincode)
                    binding.etPincode.setSelection(trimmedPincode.length)
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

    }
    private fun setupCharacterCounter() {
        binding.etContentAddress.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val charCount = s?.length ?: 0
                binding.tvCharCounter.text = "$charCount/100"

                if (charCount > 100) {
                    Toast.makeText(requireContext(), "Character limit exceeded. Maximum 100 characters allowed.", Toast.LENGTH_SHORT).show()
                    val trimmedText = s?.substring(0, 100)
                    binding.etContentAddress.setText(trimmedText)
                    binding.etContentAddress.setSelection(trimmedText?.length ?: 0)
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }



}