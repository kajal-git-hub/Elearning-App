package com.student.competishun.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.student.competishun.R
import com.student.competishun.data.model.SubjectContentItem
import com.student.competishun.databinding.FragmentSubjectContentBinding
import com.student.competishun.ui.adapter.ExploreCourseAdapter
import com.student.competishun.ui.adapter.SubjectContentAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SubjectContentFragment : Fragment() {

    private var _binding: FragmentSubjectContentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSubjectContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val subjectContentList = listOf(
            SubjectContentItem(1, "Trigonometric ratios", "08 Learning Material"),
            SubjectContentItem(2, "Pythagorean theorem", "05 Learning Material"),
            SubjectContentItem(3, "Vectors and scalars", "07 Learning Material"),
            SubjectContentItem(4, "Algebraic expressions", "10 Learning Material"),
            SubjectContentItem(5, "Calculus basics", "12 Learning Material"),
            SubjectContentItem(6, "Linear equations", "09 Learning Material"),
            SubjectContentItem(7, "Probability", "06 Learning Material"),
            SubjectContentItem(8, "Statistics", "08 Learning Material")
        )

        binding.rvSubjectContent.adapter = SubjectContentAdapter(subjectContentList){courseitem->
            findNavController().navigate(R.id.action_subjectContentFragment_to_TopicTYPEContentFragment)
        }
        binding.rvSubjectContent.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        binding.downChooseTopic.setOnClickListener {
            val bottomSheet = BottomsheetCourseTopicTypeFragment()
            bottomSheet.show(childFragmentManager, "BottomsheetCourseTopicTypeFragment")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
