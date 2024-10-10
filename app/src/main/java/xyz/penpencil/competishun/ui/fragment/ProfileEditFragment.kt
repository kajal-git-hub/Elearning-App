package xyz.penpencil.competishun.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.apollographql.apollo3.api.Optional
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.student.competishun.gatekeeper.type.UpdateUserInput
import xyz.penpencil.competishun.databinding.FragmentProfileEditBinding
import xyz.penpencil.competishun.ui.adapter.SelectClassAdapter
import xyz.penpencil.competishun.ui.adapter.SelectExamAdapter
import xyz.penpencil.competishun.ui.adapter.SelectYearAdapter
import dagger.hilt.android.AndroidEntryPoint
import xyz.penpencil.competishun.ui.main.HomeActivity
import xyz.penpencil.competishun.ui.viewmodel.UpdateUserViewModel
import xyz.penpencil.competishun.utils.SharedPreferencesManager

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
        val examList = listOf("NEET", "IIT-JEE", "Board", "UCET", "Others")
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
//        val bundle = Bundle()
//        bundle.putString("StudentClass",selectedClass)

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