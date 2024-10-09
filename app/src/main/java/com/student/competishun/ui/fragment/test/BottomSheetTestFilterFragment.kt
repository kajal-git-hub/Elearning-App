package com.student.competishun.ui.fragment.test

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.student.competishun.R
import com.student.competishun.databinding.FragmentTestFilterBinding


class BottomSheetTestFilterFragment : BottomSheetDialogFragment() {

    private val binding by lazy {
        FragmentTestFilterBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root


    override fun getTheme() = R.style.RoundedBackgroundBottomSheetDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clickListener()
    }


    private fun clickListener(){
        binding.close.setOnClickListener { dismiss() }
        binding.btnCancel.setOnClickListener { dismiss() }
    }

}