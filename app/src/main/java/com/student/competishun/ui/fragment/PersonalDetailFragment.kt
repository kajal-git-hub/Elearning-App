package com.student.competishun.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.student.competishun.R
import com.student.competishun.databinding.FragmentPersonalDetailBinding

class PersonalDetailsFragment : Fragment() {

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

        binding.spinnerTshirtSize.setOnClickListener {
            val bottomSheet = BottomSheetTSizeFragment()
            bottomSheet.show(childFragmentManager, "BottomSheetTSizeFragment")
        }
        binding.btnAddDetails.setOnClickListener {
            findNavController().navigate(R.id.action_PersonalDetails_to_AdditionalDetail)
        }

    }
}