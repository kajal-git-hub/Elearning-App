package com.student.competishun.ui.fragment.test

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import com.student.competishun.data.model.TestItem
import com.student.competishun.ui.adapter.ItemClickListener
import xyz.penpencil.competishun.ui.adapter.test.TestListAdapter
import com.student.competishun.ui.adapter.TestTypeAdapter
import xyz.penpencil.competishun.databinding.FragmentTestDashboardBinding
import xyz.penpencil.competishun.ui.main.HomeActivity

class TestDashboardFragment : Fragment() {

    private var _binding: FragmentTestDashboardBinding? = null
    private val binding get() = _binding!!

    private var testTypeAdapter : TestTypeAdapter?=null
    private var testListAdapter : TestListAdapter?=null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTestDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? HomeActivity)?.showBottomNavigationView(false)
        (activity as? HomeActivity)?.showFloatingButton(false)
        initAdapter()
        clickListener()
    }

    private fun clickListener(){
        binding.back.setOnClickListener { findNavController().popBackStack() }
    }


    private fun initAdapter(){
        testTypeAdapter = TestTypeAdapter(listOf(TestItem("JEE-Mains", true),
            TestItem("JEE-Advanced", true),
            TestItem("NEET-UG", false),
            TestItem("AIIMS", true)), object : ItemClickListener {
            override fun onItemClick(isFirst: Boolean, item: TestItem) {

            }
        })
        binding.rvTestType.layoutManager = LinearLayoutManager(context, HORIZONTAL, false)
        binding.rvTestType.adapter = testTypeAdapter

        testListAdapter = TestListAdapter(listOf(TestItem("Filter", true),
            TestItem("JEE-Mains", false),
            TestItem("JEE-Advanced", false),
            TestItem("NEET", false),
            TestItem("NEET", false)))
        binding.rvTestList.layoutManager = GridLayoutManager(context, 2)
        binding.rvTestList.adapter = testListAdapter

        binding.filter.setOnClickListener { requireActivity().supportFragmentManager?.let {
            val filter = BottomSheetTestFilterFragment {

            }
            filter.show(it, "")
        } }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}