package xyz.penpencil.competishun.ui.fragment.test

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import androidx.recyclerview.widget.RecyclerView
import com.student.competishun.data.model.TestItem
import xyz.penpencil.competishun.databinding.FragmentAcademicTestBinding

import xyz.penpencil.competishun.ui.adapter.test.AcademicTestAdapter
import xyz.penpencil.competishun.ui.adapter.test.AcademicTestResumeAdapter
import xyz.penpencil.competishun.utils.HelperFunctions


class AcademicTestFragment : Fragment() {

    private var _binding: FragmentAcademicTestBinding? = null
    private val binding get() = _binding!!
    private var listTestAdapter: AcademicTestAdapter?=null
    private var listTestResumeAdapter: AcademicTestResumeAdapter?=null

    private val helperFunctions: HelperFunctions = HelperFunctions()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAcademicTestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()

        binding.back.setOnClickListener { it.findNavController().popBackStack() }
    }


    private fun initAdapter(){
        listTestResumeAdapter = AcademicTestResumeAdapter(listOf(
            TestItem("Filter", true),
            TestItem("JEE-Mains", false),
            TestItem("JEE-Advanced", false),
            TestItem("NEET-UG", false),
            TestItem("AIIMS", false)
        ))
        helperFunctions.setupDotsIndicator(
            requireContext(),
            5,
            binding.llDotsIndicatorPromoBanner
        )
        binding.rvResumeTest.layoutManager = LinearLayoutManager(context, HORIZONTAL, false)
        binding.rvResumeTest.adapter = listTestResumeAdapter

        binding.rvResumeTest.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                helperFunctions.updateDotsIndicator(recyclerView, binding.llDotsIndicatorPromoBanner)
            }
        })

        listTestAdapter = AcademicTestAdapter(listOf(
            TestItem("Filter", true),
            TestItem("JEE-Mains", false),
            TestItem("JEE-Advanced", false),
            TestItem("NEET-UG", false),
            TestItem("AIIMS", false)
        ))
        binding.rvTest.layoutManager = LinearLayoutManager(context,
            LinearLayoutManager.VERTICAL, false)
        binding.rvTest.adapter = listTestAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}