package com.student.competishun.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.student.competishun.databinding.FragmentBottomsheetDownloadCompletedBinding

class BottomsheetDownloadCompletedFragment : BottomSheetDialogFragment() {

    private val binding by lazy {
        FragmentBottomsheetDownloadCompletedBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }
}