package com.student.competishun.ui.fragment

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.student.competishun.data.model.TSizeModel
import com.student.competishun.databinding.FragmentBottomSheetTSizeBinding
import com.student.competishun.ui.adapter.TshirtSizeAdapter
import com.student.competishun.ui.main.HomeActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BottomSheetTSizeFragment : BottomSheetDialogFragment() {

    interface OnTSizeSelectedListener {
        fun onTSizeSelected(size: String)
    }

    private var _binding: FragmentBottomSheetTSizeBinding? = null
    private val binding get() = _binding!!
    private var tSizeSelectedListener: OnTSizeSelectedListener? = null
    private var selectedSize: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomSheetTSizeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        (activity as? HomeActivity)?.showBottomNavigationView(false)
        (activity as? HomeActivity)?.showFloatingButton(false)

        selectedSize = arguments?.getString("selectedSize")


        val tSizeList = listOf(
            TSizeModel("Small (S)(36)"),
            TSizeModel("Medium (M)(38)"),
            TSizeModel("Large (L)(40)"),
            TSizeModel("Extra Large (XL)(42)"),
            TSizeModel("Extra - Extra Large (XXL)(44)"),
            TSizeModel("Extra - Extra Large (XXXL)(46)")
        )

        val tshirtSizeAdapter = TshirtSizeAdapter(tSizeList, selectedSize) { selectedSize ->
            this.selectedSize = selectedSize
            tSizeSelectedListener?.onTSizeSelected(selectedSize)
        }

        binding.rvTSize.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = tshirtSizeAdapter
        }
    }

    fun setOnTSizeSelectedListener(listener: OnTSizeSelectedListener) {
        tSizeSelectedListener = listener
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        (parentFragment as? PersonalDetailsFragment)?.let {
            it.isBottomSheetShowing = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
