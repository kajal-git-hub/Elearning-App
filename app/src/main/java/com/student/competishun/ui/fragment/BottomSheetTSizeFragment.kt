package com.student.competishun.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.student.competishun.data.model.TSizeModel
import com.student.competishun.databinding.FragmentBottomSheetTSizeBinding
import com.student.competishun.ui.adapter.TshirtSizeAdapter

class BottomSheetTSizeFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomSheetTSizeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomSheetTSizeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tSizeList = listOf(
            TSizeModel("Small (S)"),
            TSizeModel("Medium (M)"),
            TSizeModel("Large (L)"),
            TSizeModel("Extra Large (XL)"),
            TSizeModel("Extra - Extra Large (XXL)")
        )

        val installmentAdapter = TshirtSizeAdapter(tSizeList)
        binding.rvTSize.apply {
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