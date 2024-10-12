package xyz.penpencil.competishun.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import xyz.penpencil.competishun.databinding.FragmentFilterStudyMaterialBinding
import xyz.penpencil.competishun.ui.adapter.ExamFilterAdapter
import xyz.penpencil.competishun.utils.SharedPreferencesManager


class FilterStudyMaterialFragment : BottomSheetDialogFragment() {
    private lateinit var binding : FragmentFilterStudyMaterialBinding
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private val filterOptions = listOf("IIT-JEE", "NEET")
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
        setupToggleRecyclerView()
        binding.clFilterTitle.setOnClickListener {

        }


    }

    private fun setupToggleRecyclerView() {
        val adapter = ExamFilterAdapter(filterOptions) { selectedOption ->
            // Handle item click (IIT_JEE or NEET)
            Log.d("SelectedBottom", "Selected: $selectedOption")
            // Perform your actions based on the selected option
        }
        binding.rvSelectExam.adapter = adapter
    }


}