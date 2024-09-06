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
import com.student.competishun.ui.viewmodel.VideourlViewModel
import com.student.competishun.utils.HelperFunctions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TopicTypeContentFragment : Fragment() {

    private var _binding: FragmentTopicTypeContentBinding? = null
    private val binding get() = _binding!!
    private val coursesViewModel: CoursesViewModel by viewModels()
    private val videourlViewModel: VideourlViewModel by viewModels()

    private lateinit var helperFunctions: HelperFunctions
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTopicTypeContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var folderId = arguments?.getString("folder_Id")
        var folder_Name = arguments?.getString("folder_Name")
        var folder_Count = arguments?.getString("folder_Count")?:"0"

        binding.tvTopicTypeName.text = folder_Name?:""
        if (folderId != null) {
            folderProgress(folderId)
        }
        helperFunctions = HelperFunctions()
        binding.backIcon.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
        binding.tvTopicContentCount.text = "0$folder_Count"

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
                        subjectIcon = R.drawable.group_1707478994,
                        id = content.content?.id.orEmpty(),// replace with appropriate icon
                        playIcon = R.drawable.play_video_icon,    // replace with appropriate icon
                        lecture = "Lecture",                      // you can also replace this with dynamic data if available
                        lecturerName = content.content?.file_name.orEmpty(),
                        topicName = content.content?.file_name.orEmpty(), // replace with dynamic data if available
                        topicDescription = content.content?.file_name.orEmpty(),
                        progress = content.videoCompletionPercentage?.toInt() ?: 0,
                        url = content.content?.file_url.orEmpty(),
                        fileType = content.content?.file_type?.name?:""
                    )

                } ?: emptyList()

                val adapter = TopicContentAdapter(topicContents, folderId) { topicContent, folderContentId ->
                    if (topicContent.fileType == "VIDEO") {
                        // Handle the click event for video file type
                        videoUrlApi(videourlViewModel, topicContent.id)
                    } else if (topicContent.fileType == "PDF"){
                        helperFunctions.showDownloadDialog(
                            requireContext(),
                            topicContent.url,
                            topicContent.topicName
                        )
                    }
                    else {
                        Log.d("TopicContentAdapter", "File type is not VIDEO: ${topicContent.fileType}")
                    }
                }

                binding.rvTopicContent.adapter = adapter
                binding.rvTopicContent.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

                val folderProgressFolder = data.findCourseFolderProgress.folder


            }.onFailure { error ->
                // Handle the error
                Log.e("AllDemoResourcesFree", error.message.toString())
            }
        }
    }
    fun videoUrlApi(viewModel: VideourlViewModel, folderContentId:String){

        viewModel.fetchVideoStreamUrl(folderContentId, "360p")

        viewModel.videoStreamUrl.observe(viewLifecycleOwner, { signedUrl ->
            Log.d("Videourl", "Signed URL: $signedUrl")
            if (signedUrl != null) {
                val bundle = Bundle().apply {
                    putString("url", signedUrl)
                }
                findNavController().navigate(R.id.mediaFragment,bundle)

            } else {
                // Handle error or null URL
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
