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
                topicName = "Introduction to Android",
                topicDescription = "This is an introductory course to Android development. You will learn about the basics of Android.",
                progress = 75
            ),
            TopicContentModel(
                subjectIcon = R.drawable.group_1707478994,
                playIcon = R.drawable.play_video_icon,
                lecture = "Lecture 2",
                lecturerName = "Prof. Jane Smith",
                topicName = "Advanced Android",
                topicDescription = "This course covers advanced topics in Android development, including performance optimization and architecture patterns.",
                progress = 50
            )
        )
//
//        val adapter = TopicContentAdapter(topicContents)
//        binding.rvTopicContent.adapter = adapter
//        binding.rvTopicContent.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
