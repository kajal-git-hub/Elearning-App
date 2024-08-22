package com.student.competishun.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.student.competishun.databinding.FragmentBottomSheetDescriptionBinding


    class BottomSheetDescriptionFragment : Fragment() {

    private val binding by lazy {
        FragmentBottomSheetDescriptionBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

}