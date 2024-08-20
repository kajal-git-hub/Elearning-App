package com.student.competishun.ui.fragment

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
import com.student.competishun.R
import com.student.competishun.databinding.FragmentAddressDetailsBinding
import com.student.competishun.databinding.FragmentPersonalDetailBinding

class AddressDetailsFragment : Fragment() {

    private var _binding: FragmentAddressDetailsBinding?=null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddressDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

}