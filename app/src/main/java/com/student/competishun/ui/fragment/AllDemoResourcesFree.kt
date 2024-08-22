package com.student.competishun.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.student.competishun.R
import com.student.competishun.data.model.FreeDemoItem
import com.student.competishun.ui.adapter.FreeDemoAdapter
import com.student.competishun.databinding.FragmentAllDemoResourcesFreeBinding

class AllDemoResourcesFree : Fragment() {

    private var _binding: FragmentAllDemoResourcesFreeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllDemoResourcesFreeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.igDemoBackButton.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
//        binding.igDemoBackButton.setOnClickListener {
//            findNavController().navigateUp()
//        }

        val freeItems = listOf(
            FreeDemoItem(R.drawable.frame_1707480717,"About this Course","15 mins"),
            FreeDemoItem(R.drawable.frame_1707480717,"Introduction","25 mins"),
            FreeDemoItem(R.drawable.frame_1707480717,"Preparation Mantra","5 mins"),
        )
        val freeDemoAdapter = FreeDemoAdapter(freeItems)

        binding.rvAllDemoFree.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = freeDemoAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
