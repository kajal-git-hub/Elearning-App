package com.student.competishun.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.student.competishun.R
import com.student.competishun.databinding.FragmentPersonalDetailBinding
import com.student.competishun.ui.viewmodel.UserViewModel

class PersonalDetailsFragment : Fragment() {
    private val userViewModel: UserViewModel by viewModels()

    private val binding by lazy {
        FragmentPersonalDetailBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val bottomSheet = BottomSheetPersonalDetailsFragment()
        bottomSheet.show(childFragmentManager, "BottomSheetPersonalDetailsFragment")

        userViewModel.userDetails.observe(viewLifecycleOwner) { result ->
            result.onSuccess { data ->
                val userDetails = data.getMyDetails
//                binding.etFullName.text = userDetails.fullName
//                mobileNumberTextView.text = userDetails.mobileNumber
//                userInformationTextView.text = userDetails.userInformation.toString() // Customize as needed
            }.onFailure { exception ->
                // Handle error
                Toast.makeText(requireContext(), "Error fetching details: ${exception.message}", Toast.LENGTH_LONG).show()
            }
        }
        userViewModel.fetchUserDetails()
        binding.etFullName
        binding.spinnerTshirtSize.setOnClickListener {
            val bottomSheet = BottomSheetTSizeFragment()
            bottomSheet.show(childFragmentManager, "BottomSheetTSizeFragment")
        }
        binding.btnAddDetails.setOnClickListener {
            findNavController().navigate(R.id.action_PersonalDetails_to_AdditionalDetail)
        }

    }
}