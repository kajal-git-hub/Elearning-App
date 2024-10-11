package xyz.penpencil.competishun.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.databinding.FragmentAddressDetailsBinding
import xyz.penpencil.competishun.ui.main.HomeActivity

class AddressDetailsFragment : DrawerVisibility() {

    private var _binding: FragmentAddressDetailsBinding?=null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddressDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        (activity as? HomeActivity)?.showBottomNavigationView(false)
        (activity as? HomeActivity)?.showFloatingButton(false)

        setupCharacterCounter()
        pinCodeCheck()

        binding.etBTUpload.setOnClickListener {
            findNavController().navigate(R.id.AdditionalDetailsFragment)
        }

        binding.btnAddressDetails.setOnClickListener {
            if(isAddressFormValid()){
                findNavController().navigate(R.id.action_AddressDetail_to_CourseEmpty)
            }else {
                Toast.makeText(requireContext(), "Please fill all fields.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        binding.etContentAddress.addTextChangedListener(textWatcher)
        binding.etPincode.addTextChangedListener(textWatcher)
        binding.etCity.addTextChangedListener(textWatcher)
        binding.etState.addTextChangedListener(textWatcher)
    }
    private fun isAddressFormValid(): Boolean {
        val flatAddress = binding.etContentAddress.text.toString()
        val pinCode = binding.etPincode.text.toString()
        val city = binding.etCity.text.toString()
        val state = binding.etState.text.toString()

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