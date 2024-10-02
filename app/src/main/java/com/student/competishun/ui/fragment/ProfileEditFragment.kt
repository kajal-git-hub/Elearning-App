package com.student.competishun.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.apollographql.apollo3.api.Optional
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.student.competishun.R
import com.student.competishun.databinding.FragmentProfileEditBinding
import com.student.competishun.gatekeeper.type.UpdateUserInput
import com.student.competishun.ui.adapter.SelectClassAdapter
import com.student.competishun.ui.adapter.SelectExamAdapter
import com.student.competishun.ui.adapter.SelectYearAdapter
import com.student.competishun.ui.main.HomeActivity
import com.student.competishun.ui.main.MainActivity
import com.student.competishun.ui.viewmodel.UpdateUserViewModel
import com.student.competishun.utils.SharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileEditFragment : BottomSheetDialogFragment() {
    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    private val updateUserViewModel: UpdateUserViewModel by viewModels()

    private lateinit var binding: FragmentProfileEditBinding

    private var selectedClass: String? = null
    private var selectedExam: String? = null
    private var selectedYear: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileEditBinding.inflate(inflater, container, false)
        sharedPreferencesManager = (requireActivity() as HomeActivity).sharedPreferencesManager
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
        val examList = listOf("NEET-UG", "IIT-JEE", "Board", "UCET", "Others")
        val yearList = listOf("2025", "2026")

        binding.rvSelectClass.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = SelectClassAdapter(classList) { selectedClass ->
                this@ProfileEditFragment.selectedClass = selectedClass
            }
        }
        binding.rvSelectExam.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = SelectExamAdapter(examList) { selectedExam ->
                this@ProfileEditFragment.selectedExam = selectedExam
            }
        }
        binding.rvSelectYear.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = SelectYearAdapter(yearList) { selectedYear ->
                this@ProfileEditFragment.selectedYear = selectedYear
            }
        }

//        val updateUserInput = UpdateUserInput(
//            city = Optional.Present(sharedPreferencesManager.city),
//            fullName = Optional.Present(sharedPreferencesManager.name),
//            preparingFor = Optional.Present(sharedPreferencesManager.preparingFor),
//            reference = Optional.Present(sharedPreferencesManager.reference),
//            targetYear = Optional.Present(sharedPreferencesManager.targetYear)
//        )


        binding.mbSaveButton.setOnClickListener {
            val updatedUserInput = UpdateUserInput(
                city = Optional.Present(sharedPreferencesManager.city),
                fullName = Optional.Present(sharedPreferencesManager.name),
                preparingFor = Optional.Present(
                    selectedExam ?: sharedPreferencesManager.preparingFor
                ),
                reference = Optional.Present(sharedPreferencesManager.reference),
                targetYear = Optional.Present(selectedYear ?: sharedPreferencesManager.targetYear)
            )
            updateUserViewModel.updateUser(updatedUserInput, null, null)
            Toast.makeText(requireContext(), "Updated Successfully", Toast.LENGTH_SHORT).show()
            dismiss()
        }
    }

}