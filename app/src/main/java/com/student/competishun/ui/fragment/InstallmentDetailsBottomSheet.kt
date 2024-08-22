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
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InstallmentDetailsBottomSheet : BottomSheetDialogFragment() {

    private var firstInstallment: Int = 0
    private var secondInstallment: Int = 0

    fun setInstallmentData(firstInstallment: Int, secondInstallment: Int) {
        this.firstInstallment = firstInstallment
        this.secondInstallment = secondInstallment
    }

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
            InstallmentModel("1st Installment", "₹$firstInstallment", installmentItemList),
            InstallmentModel("2nd Installment", "₹$secondInstallment", installmentItemList),
        )
        var total = firstInstallment?.toInt()?.plus(secondInstallment?.toInt()?:0)
        binding.dicountPricexp.text = total.toString()
        binding.buyNow.setOnClickListener{
          dismiss()
        }
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
