package com.student.competishun.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.student.competishun.R
import com.student.competishun.data.model.TopicContentModel
import com.student.competishun.databinding.FragmentTopicTypeContentBinding
import com.student.competishun.ui.adapter.TopicContentAdapter
import com.student.competishun.ui.viewmodel.CoursesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TopicTypeContentFragment : Fragment() {

    private var _binding: FragmentTopicTypeContentBinding? = null
    private val binding get() = _binding!!
    private val coursesViewModel: CoursesViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTopicTypeContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        folderProgress("b8b9cb32-661b-4e8e-90d0-9c5a0740d273")
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
        binding.backIcon.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
        val adapter = TopicContentAdapter(topicContents)
        binding.rvTopicContent.adapter = adapter
        binding.rvTopicContent.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)

    }

    fun folderProgress(folderId:String){
        if (folderId != null) {
            coursesViewModel.findCourseFolderProgress(folderId)
        }
        coursesViewModel.courseFolderProgress.observe(viewLifecycleOwner) { result ->
            result.onSuccess { data ->
                Log.e("GetFolderdata", data.findCourseFolderProgress.folder.toString())

                val folderContents = data.findCourseFolderProgress.folderContents

                val topicContents = folderContents?.map { content ->
                    TopicContentModel(
                        subjectIcon = R.drawable.group_1707478994, // replace with appropriate icon
                        playIcon = R.drawable.play_video_icon,    // replace with appropriate icon
                        lecture = "Lecture",                      // you can also replace this with dynamic data if available
                        lecturerName = content.content?.file_name.orEmpty(),
                        topicName = "Applying Trig Functions to Angles of Rotation", // replace with dynamic data if available
                        topicDescription = "",
                        progress = content.videoCompletionPercentage?.toInt() ?: 0
                    )
                } ?: emptyList()
                val adapter = TopicContentAdapter(topicContents)
                binding.rvTopicContent.adapter = adapter
                binding.rvTopicContent.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)


                val folderProgressFolder = data.findCourseFolderProgress.folder


            }.onFailure { error ->
                // Handle the error
                Log.e("AllDemoResourcesFree", error.message.toString())
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
