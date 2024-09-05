package com.student.competishun.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.student.competishun.data.model.TopicTypeModel
import com.student.competishun.databinding.FragmentBottomsheetCourseTopicTypeBinding
import com.student.competishun.ui.adapter.TopicTypeAdapter
import com.student.competishun.ui.main.HomeActivity


class BottomsheetCourseTopicTypeFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomsheetCourseTopicTypeBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomsheetCourseTopicTypeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        (activity as? HomeActivity)?.showBottomNavigationView(false)
        (activity as? HomeActivity)?.showFloatingButton(false)

        val TopicTypeList = listOf(
            TopicTypeModel("Basic Maths (08)"),
            TopicTypeModel("Quadratic Equations (08)"),
            TopicTypeModel("Algebra (08)"),
            TopicTypeModel("Trigonometry (08)"),
            TopicTypeModel("Geometry (08)"),
            TopicTypeModel("Calculus (08)"),
            TopicTypeModel("Statistics (08)"),
            TopicTypeModel("Probability (08)"),
        )

        val installmentAdapter = TopicTypeAdapter(TopicTypeList)
        binding.rvTopicTypes.apply {
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