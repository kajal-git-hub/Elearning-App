package xyz.penpencil.competishun.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import xyz.penpencil.competishun.databinding.FragmentFilterStudyMaterialBinding
import xyz.penpencil.competishun.ui.adapter.ExamFilterAdapter
import xyz.penpencil.competishun.ui.adapter.SubjectFilterAdapter
import xyz.penpencil.competishun.utils.FilterSelectionListener
import xyz.penpencil.competishun.utils.SharedPreferencesManager


class FilterStudyMaterialFragment : BottomSheetDialogFragment(){
    private lateinit var binding : FragmentFilterStudyMaterialBinding
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private val filterOptions = listOf("IIT-JEE", "NEET")
    private val filterClass = listOf("11th", "12th","12+")
    private var selectedExamOption: String? = null
    private var listener: FilterSelectionListener? = null
    private var selectedSubjectOption: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFilterStudyMaterialBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvSelectExam.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSelectSubject.layoutManager = LinearLayoutManager(requireContext())
        setupToggleRecyclerView()
        setupSubjectRecyclerView()
        Log.e("slelcted",selectedExamOption.toString() + selectedSubjectOption)
        if (!selectedExamOption.isNullOrEmpty()) {
            binding.mbLogoutButton.text = "Apply Filter (1)"
        } else if (!selectedSubjectOption.isNullOrEmpty()) {
            binding.mbLogoutButton.text = "Apply Filter (1)"
        } else if (!selectedSubjectOption.isNullOrEmpty() && !selectedExamOption.isNullOrEmpty()) {
            binding.mbLogoutButton.text = "Apply Filter (2)"
        } else  binding.mbLogoutButton.text = "Apply Filter"
        binding.mbLogoutButton.setOnClickListener {
            listener?.onFiltersSelected(selectedExamOption, selectedSubjectOption)
            dismiss()
        }

        binding.mbCancel.setOnClickListener {
            dismiss()
        }

        binding.closeFilter.setOnClickListener {
            dismiss()
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val parentFragment = parentFragment  // Get the parent fragment

        // Check if the parent fragment implements the FilterSelectionListener
        if (parentFragment is FilterSelectionListener) {
            listener = parentFragment
        } else {
            throw RuntimeException("$parentFragment must implement FilterSelectionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private fun setupToggleRecyclerView() {
        val adapter = ExamFilterAdapter(filterOptions) { selectedOption ->
            // Handle item click (IIT_JEE or NEET)
            selectedExamOption = selectedOption
            if (selectedSubjectOption.isNullOrEmpty())
                binding.mbLogoutButton.text = "Apply Filter (1)"
            else  binding.mbLogoutButton.text = "Apply Filter (2)"
            Log.d("SelectedBottom", "Selected: $selectedOption")
            // Perform your actions based on the selected option
        }
        binding.rvSelectExam.adapter = adapter
    }

    private fun setupSubjectRecyclerView() {
        val adapter = SubjectFilterAdapter(filterClass) { selectedOption ->
            // Handle item click (11th or 12th)
            selectedSubjectOption = selectedOption
            if (selectedExamOption.isNullOrEmpty())
            binding.mbLogoutButton.text = "Apply Filter (1)"
            else  binding.mbLogoutButton.text = "Apply Filter (2)"
            Log.d("SelectedSubj", "Selected: $selectedOption $selectedSubjectOption")
            // Perform your actions based on the selected option
        }
        binding.rvSelectSubject.adapter = adapter
    }



}