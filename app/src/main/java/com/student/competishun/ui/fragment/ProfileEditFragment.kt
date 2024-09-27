package com.student.competishun.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.student.competishun.R
import com.student.competishun.databinding.FragmentProfileEditBinding
import com.student.competishun.ui.adapter.SelectClassAdapter
import com.student.competishun.ui.adapter.SelectExamAdapter
import com.student.competishun.ui.adapter.SelectYearAdapter

class ProfileEditFragment : BottomSheetDialogFragment() {

    private lateinit var binding : FragmentProfileEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileEditBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.closeBottomClass.setOnClickListener {
            dismiss()
        }
        binding.mbCancel.setOnClickListener {
            dismiss()
        }

        val classList = listOf("11th", "12th", "12th+")

        binding.rvSelectClass.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = SelectClassAdapter(classList)
        }
        val examList = listOf("NEET-UG","IIT-JEE","Board","UCET","Others")
        binding.rvSelectExam.apply {
            layoutManager = GridLayoutManager(context,2)
            adapter = SelectExamAdapter(examList)
        }
        val yearList = listOf("2025","2026")
        binding.rvSelectYear.apply {
            layoutManager = GridLayoutManager(context,2)
            adapter = SelectYearAdapter(yearList)
        }


    }

}