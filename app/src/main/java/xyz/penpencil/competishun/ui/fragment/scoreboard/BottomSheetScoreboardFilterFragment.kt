package xyz.penpencil.competishun.ui.fragment.scoreboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.databinding.FragmentScoreboardFilterBinding


class BottomSheetScoreboardFilterFragment(val submit:(list: FilterData)->Unit) : BottomSheetDialogFragment() {

    private val binding by lazy {
        FragmentScoreboardFilterBinding.inflate(layoutInflater)
    }

    private var selectedExams: MutableList<String> = mutableListOf()
    private var selectedStatus: MutableList<String> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun getTheme() = R.style.RoundedBackgroundBottomSheetDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clickListener()
    }

    private fun clickListener() {
        binding.close.setOnClickListener { dismiss() }

        binding.btnCancel.setOnClickListener { dismiss() }

        binding.btnApply.setOnClickListener {
            applyFilter()
        }

        binding.cbNeet.setOnCheckedChangeListener { _, isChecked ->
            toggleSelection(isChecked, "NEET-UG", selectedExams)
        }

        binding.cbAiims.setOnCheckedChangeListener { _, isChecked ->
            toggleSelection(isChecked, "AIIMS", selectedExams)
        }

        binding.cbJeeMains.setOnCheckedChangeListener { _, isChecked ->
            toggleSelection(isChecked, "JEE-Mains", selectedExams)
        }

        binding.cbJeeAdvance.setOnCheckedChangeListener { _, isChecked ->
            toggleSelection(isChecked, "JEE-Advanced", selectedExams)
        }
    }

    private fun toggleSelection(isChecked: Boolean, value: String, list: MutableList<String>) {
        if (isChecked) {
            list.add(value)
        } else {
            list.remove(value)
        }
    }

    private fun applyFilter() {
        val filterData = FilterData(
            exams = selectedExams,
            year = "",
            status = selectedStatus
        )
        dismiss()
        submit(filterData)
    }

    private fun increaseYear(currentYear: String): String {
        val years = currentYear.split("-").map { it.toInt() }
        val nextYear = "${years[0] + 1}-${years[1] + 1}"
        return nextYear
    }

    private fun decreaseYear(currentYear: String): String {
        val years = currentYear.split("-").map { it.toInt() }
        val prevYear = "${years[0] - 1}-${years[1] - 1}"
        return prevYear
    }

    data class FilterData(
        val exams: List<String>,
        val year: String,
        val status: List<String>
    )
}