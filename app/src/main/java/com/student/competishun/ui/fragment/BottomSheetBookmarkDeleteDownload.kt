package com.student.competishun.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.student.competishun.R
import com.student.competishun.databinding.FragmentBottomSheetBookmarkDeleteDownloadBinding

class BottomSheetBookmarkDeleteDownload : BottomSheetDialogFragment() {

    private lateinit var binding : FragmentBottomSheetBookmarkDeleteDownloadBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomSheetBookmarkDeleteDownloadBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvBmDownloadThing.setOnClickListener {
            val bottomSheetDownloadOptions = BottomSheetVideoQualityFragment()
            bottomSheetDownloadOptions.show(childFragmentManager, bottomSheetDownloadOptions.tag)
        }
        binding.tvBmDeleteThing.setOnClickListener {

        }

    }

}