package xyz.penpencil.competishun.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.apollographql.apollo3.api.Optional
import com.student.competishun.gatekeeper.MyDetailsQuery
import dagger.hilt.android.AndroidEntryPoint
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.databinding.FragmentAddressDetailsBinding
import xyz.penpencil.competishun.ui.main.HomeActivity
import xyz.penpencil.competishun.ui.viewmodel.UpdateUserViewModel
import xyz.penpencil.competishun.ui.viewmodel.UserViewModel
import xyz.penpencil.competishun.utils.SharedPreferencesManager

@AndroidEntryPoint
class AddressDetailsFragment : DrawerVisibility() {

    private var _binding: FragmentAddressDetailsBinding? = null
    private val binding get() = _binding!!
    lateinit var sharedPreferencesManager: SharedPreferencesManager
    private val updateUserViewModel: UpdateUserViewModel by viewModels()

    private val userViewModel: UserViewModel by viewModels()

    private var fieldsToVisible = emptyArray<String>()
    private var courseId: String = ""
    var userDetails: MyDetailsQuery.GetMyDetails?=null

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
        val homeActivity = activity as? HomeActivity
        homeActivity?.apply {
            showBottomNavigationView(false)
            showFloatingButton(false)
        }

        arguments?.let {
            fieldsToVisible = it.getStringArray("FIELD_REQUIRED") ?: emptyArray()
            courseId = it.getString("IDS", "")
        }

        binding.etState.setText(sharedPreferencesManager.state)
        binding.etCity.setText(sharedPreferencesManager.city)

        setupTextWatchers()
        setupCharacterCounter()
        pinCodeCheck()

        binding.etBTUpload.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnAddressDetails.setOnClickListener {
            if (isAddressFormValid()) {
                updateUser()
            } else {
                showToast(getString(R.string.fill_all_fields))
            }
        }

        observeViewModel()
        userViewModel.fetchUserDetails()
    }

    private fun isAddressFormValid(): Boolean {
        return binding.etContentAddress.text.isNotEmpty() &&
                binding.etPincode.text.isNotEmpty() &&
                binding.etCity.text.isNotEmpty() &&
                binding.etState.text.isNotEmpty()
    }

    private fun saveAddressData() {
        sharedPreferencesManager.apply {
            putString("current$courseId", "")
            putBoolean(courseId, true)
        }
    }

    private fun updateUser() {
        val cityToEnter = binding.etCity.text.toString().ifEmpty { sharedPreferencesManager.city }
        val stateToEnter = binding.etState.text.toString().ifEmpty { sharedPreferencesManager.state }

        val updateUserInput = com.student.competishun.gatekeeper.type.UpdateUserInput(
            pinCode = Optional.present(binding.etPincode.text.toString()),
            addressLine1 = Optional.present(binding.etContentAddress.text.toString()),
            city = Optional.present(cityToEnter),
            state = Optional.present(stateToEnter)
        )

        updateUserViewModel.updateUser(updateUserInput, null, null)
    }

    private fun observeViewModel() {
        updateUserViewModel.updateUserResult.observe(viewLifecycleOwner) { result ->
            if (result!=null){
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.nav_graph_chome, true)
                    .build()

                findNavController().navigate(R.id.courseEmptyFragment, null, navOptions)
            }else {
                showToast("try again later")
            }
        }

        userViewModel.userDetails.observe(viewLifecycleOwner) { result ->
            result.onSuccess { data ->
                userDetails = data.getMyDetails
                initializeUpdateUI()
            }.onFailure {
                Log.e("Error fetching details", it.message.toString())
            }
        }
    }

    /**
     * Populate UI
     * */
    private fun initializeUpdateUI(){
        binding.etContentAddress.setText(userDetails?.userInformation?.address?.addressLine1?:"")
        binding.etPincode.setText(userDetails?.userInformation?.address?.pinCode?:"")
        binding.etCity.setText(userDetails?.userInformation?.city?:"")
        binding.etState.setText(userDetails?.userInformation?.address?.state?:"")
    }

    private fun pinCodeCheck() {
        binding.etPincode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    if (it.length > 6) {
                        showToast(getString(R.string.pincode_limit))
                        binding.etPincode.setText(it.substring(0, 6))
                        binding.etPincode.setSelection(6)
                    }
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupTextWatchers() {
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = updateButtonState()
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        with(binding) {
            etContentAddress.addTextChangedListener(textWatcher)
            etPincode.addTextChangedListener(textWatcher)
            etCity.addTextChangedListener(textWatcher)
            etState.addTextChangedListener(textWatcher)
        }
    }

    private fun updateButtonState() {
        val isValid = isAddressFormValid()
        binding.btnAddressDetails.apply {
            setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (isValid) R.color.blue_3E3EF7 else R.color.gray_border
                )
            )
            isEnabled = isValid
        }
    }

    private fun setupCharacterCounter() {
        binding.etContentAddress.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val charCount = s?.length ?: 0
                binding.tvCharCounter.text = "$charCount/100"

                if (charCount > 100) {
                    showToast(getString(R.string.char_limit_exceeded))
                    binding.etContentAddress.setText(s?.substring(0, 100))
                    binding.etContentAddress.setSelection(100)
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}