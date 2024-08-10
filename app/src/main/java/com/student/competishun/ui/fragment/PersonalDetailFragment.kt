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
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PersonalDetailsFragment : Fragment() {
    private val userViewModel: UserViewModel by viewModels()
    private var _binding: FragmentPersonalDetailBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPersonalDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val bottomSheet = BottomSheetPersonalDetailsFragment()
        bottomSheet.show(childFragmentManager, "BottomSheetPersonalDetailsFragment")

        userViewModel.userDetails.observe(viewLifecycleOwner) { result ->
            result.onSuccess { data ->
                val userDetails = data.getMyDetails
            }.onFailure { exception ->
                // Handle error
                Toast.makeText(requireContext(), "Error fetching details: ${exception.message}", Toast.LENGTH_LONG).show()
            }
        }
        userViewModel.fetchUserDetails()
        binding.spinnerTshirtSize.setOnClickListener {
            val bottomSheet = BottomSheetTSizeFragment()
            bottomSheet.show(childFragmentManager, "BottomSheetTSizeFragment")
        }
        binding.btnAddDetails.setOnClickListener {
            findNavController().navigate(R.id.action_PersonalDetails_to_AdditionalDetail)
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}