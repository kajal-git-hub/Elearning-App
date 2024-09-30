package com.student.competishun.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.student.competishun.R
import com.student.competishun.databinding.FragmentBottomSheetVideoQualityBinding

class BottomSheetVideoQualityFragment : BottomSheetDialogFragment() {

    private lateinit var binding : FragmentBottomSheetVideoQualityBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentBottomSheetVideoQualityBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

}