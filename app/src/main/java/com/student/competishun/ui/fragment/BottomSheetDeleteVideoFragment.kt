package com.student.competishun.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.student.competishun.R
import com.student.competishun.databinding.FragmentBottomSheetDeleteVideoBinding
import com.student.competishun.utils.OnDeleteClickListener

class BottomSheetDeleteVideoFragment : BottomSheetDialogFragment() {

    private lateinit var binding : FragmentBottomSheetDeleteVideoBinding

    private var listener: OnDeleteClickListener? = null
    private var itemPosition: Int = -1

    fun setListener(listener: OnDeleteClickListener, position: Int) {
        this.listener = listener
        this.itemPosition = position
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomSheetDeleteVideoBinding.inflate(inflater,container,false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.clDeleteButton.setOnClickListener {
            if (itemPosition >= 0) {
                listener?.onDeleteClick(itemPosition)
            }
            dismiss()
        }
        binding.clCancelButton.setOnClickListener {
            dismiss()
        }
    }

}