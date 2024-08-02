package com.student.competishun.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.student.competishun.data.model.InstallmentItemModel
import com.student.competishun.data.model.InstallmentModel
import com.student.competishun.databinding.BottomSheetInstallmentDetailsBinding
import com.student.competishun.ui.adapter.InstallmentAdapter

class InstallmentDetailsBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetInstallmentDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetInstallmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val installmentItemList = listOf(
            InstallmentItemModel("On the day of purchase of the course."),
            InstallmentItemModel("On the day of purchase of the course."),
        )

        val installmentList = listOf(
            InstallmentModel("1st Installment", "₹16,500", installmentItemList),
            InstallmentModel("2nd Installment", "₹16,500", installmentItemList),
        )

        val installmentAdapter = InstallmentAdapter(installmentList)
        binding.rvInstallment.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = installmentAdapter
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
