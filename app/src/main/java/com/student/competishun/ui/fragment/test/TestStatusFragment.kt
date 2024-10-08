package com.student.competishun.ui.fragment.test

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.student.competishun.R
import com.student.competishun.databinding.FragmentTestStatusBinding
import com.student.competishun.ui.adapter.test.TestStatusAdapter

class TestStatusFragment : DialogFragment() {

    private var _binding: FragmentTestStatusBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTestStatusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        return Dialog(requireContext(), R.style.FullScreenDialogStyle)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

            decorView?.systemUiVisibility =
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            setBackgroundDrawableResource(android.R.color.transparent)
        }
        clickListener()
        setTestAdapter()
    }


    private fun clickListener(){
        binding.close.setOnClickListener { dismiss() }
    }

    private fun setTestAdapter(){
        val items = listOf(
            "Physics",
            TestStatusAdapter.GridItem(R.drawable.ic_correct_ans_green),
            TestStatusAdapter.GridItem(R.drawable.ic_wrong_ans_red),
            TestStatusAdapter.GridItem(R.drawable.ic_correct_ans_green),
            TestStatusAdapter.GridItem(R.drawable.ic_wrong_ans_red),
            TestStatusAdapter.GridItem(R.drawable.ic_correct_ans_green),
            TestStatusAdapter.GridItem(R.drawable.ic_wrong_ans_red),
            TestStatusAdapter.GridItem(R.drawable.ic_correct_ans_green),
            TestStatusAdapter.GridItem(R.drawable.ic_wrong_ans_red),
            "Maths",
            TestStatusAdapter.GridItem(R.drawable.ic_correct_ans_green),
            TestStatusAdapter.GridItem(R.drawable.ic_wrong_ans_red),
            TestStatusAdapter.GridItem(R.drawable.ic_correct_ans_green),
            TestStatusAdapter.GridItem(R.drawable.ic_wrong_ans_red),
            TestStatusAdapter.GridItem(R.drawable.ic_correct_ans_green),
            TestStatusAdapter.GridItem(R.drawable.ic_wrong_ans_red),
            TestStatusAdapter.GridItem(R.drawable.ic_correct_ans_green),
            TestStatusAdapter.GridItem(R.drawable.ic_wrong_ans_red),
            "Chemistry",
            TestStatusAdapter.GridItem(R.drawable.ic_correct_ans_green),
            TestStatusAdapter.GridItem(R.drawable.ic_wrong_ans_red),
            TestStatusAdapter.GridItem(R.drawable.ic_correct_ans_green),
            TestStatusAdapter.GridItem(R.drawable.ic_wrong_ans_red),
            TestStatusAdapter.GridItem(R.drawable.ic_correct_ans_green),
            TestStatusAdapter.GridItem(R.drawable.ic_wrong_ans_red),
            TestStatusAdapter.GridItem(R.drawable.ic_correct_ans_green),
            TestStatusAdapter.GridItem(R.drawable.ic_wrong_ans_red)
        )

        val adapter = TestStatusAdapter(items)
        binding.rvTestStatus.adapter = adapter
        val layoutManager = GridLayoutManager(requireContext(), 5)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (adapter.getItemViewType(position) == TestStatusAdapter.TYPE_TITLE) 5 else 1
            }
        }
        binding.rvTestStatus.layoutManager = layoutManager
    }
}

