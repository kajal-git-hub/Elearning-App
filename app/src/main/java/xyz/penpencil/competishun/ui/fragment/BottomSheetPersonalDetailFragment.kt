package xyz.penpencil.competishun.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import xyz.penpencil.competishun.databinding.FragmentBottomSheetPersonalDetailBinding


class BottomSheetPersonalDetailsFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomSheetPersonalDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentBottomSheetPersonalDetailBinding.inflate(inflater, container, false)
        return binding.root
    }
}