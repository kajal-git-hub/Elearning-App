package com.student.competishun.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.student.competishun.R
import com.student.competishun.data.model.TopicContentModel
import com.student.competishun.databinding.FragmentTopicTypeContentBinding
import com.student.competishun.ui.adapter.TopicContentAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TopicTypeContentFragment : Fragment() {

    private var _binding: FragmentTopicTypeContentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTopicTypeContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val topicContents = listOf(
            TopicContentModel(
                subjectIcon = R.drawable.group_1707478994,
                playIcon = R.drawable.play_video_icon,
                lecture = "Lecture",
                lecturerName = "Prof. John Doe",
                topicName = "Applying Trig Functions to Angles of Rotation",
                topicDescription = "Relations and Functions: Types of relations and types and relat\n" +
                        "tions.Relations and Functions: Types of relations and functions, & Inverse trigonometric functions.\n" +
                        "s and...",
                progress = 75
            ),
            TopicContentModel(
                subjectIcon = R.drawable.group_1707478994,
                playIcon = R.drawable.play_video_icon,
                lecture = "Lecture 2",
                lecturerName = "Prof. Jane Smith",
                topicName = "Applying Trig Functions to Angles of Rotation",
                topicDescription = "Relations and Functions: Types of relations and types and relat\n" +
                        "tions.Relations and Functions: Types of relations and functions, & Inverse trigonometric functions.\n" +
                        "s and...",
                progress = 50
            )
        )

        val adapter = TopicContentAdapter(topicContents)
        binding.rvTopicContent.adapter = adapter
        binding.rvTopicContent.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
