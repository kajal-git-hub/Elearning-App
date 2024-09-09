package com.student.competishun.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.student.competishun.R
import com.student.competishun.curator.FindCourseFolderProgressQuery
import com.student.competishun.curator.type.FileType
import com.student.competishun.curator.type.UpdateVideoProgress
import com.student.competishun.data.model.FreeDemoItem
import com.student.competishun.data.model.SubjectContentItem
import com.student.competishun.data.model.TopicContentModel
import com.student.competishun.data.model.TopicTypeModel
import com.student.competishun.databinding.FragmentSubjectContentBinding
import com.student.competishun.di.SharedVM
import com.student.competishun.ui.adapter.SubjectContentAdapter
import com.student.competishun.ui.adapter.TopicContentAdapter
import com.student.competishun.ui.main.HomeActivity
import com.student.competishun.ui.viewmodel.CoursesViewModel
import com.student.competishun.ui.viewmodel.VideourlViewModel
import com.student.competishun.utils.HelperFunctions
import com.student.competishun.utils.OnTopicTypeSelectedListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SubjectContentFragment : Fragment() {

    private lateinit var binding: FragmentSubjectContentBinding
    private val coursesViewModel: CoursesViewModel by viewModels()
    private lateinit var helperFunctions: HelperFunctions
    private val videourlViewModel: VideourlViewModel by viewModels()
    var VwatchedDuration:Int = 0
    private lateinit var sharedViewModel: SharedVM
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSubjectContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backIconSubjectContent.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        helperFunctions = HelperFunctions()

        (activity as? HomeActivity)?.showBottomNavigationView(false)
        (activity as? HomeActivity)?.showFloatingButton(false)



       // val folderId = arguments?.getString("folderId")



        // Retrieve arguments passed to the fragment

        val folderIds = arguments?.getStringArrayList("folder_ids") ?: arrayListOf()
        val folderNames = arguments?.getStringArrayList("folder_names") ?: arrayListOf()
        val courseName = arguments?.getString("courseName") ?: ""
        val folderName = arguments?.getString("folder_Name") ?: ""
        var folder_Count = arguments?.getString("folder_Count")?:"0"




        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedVM::class.java)
        sharedViewModel.watchedDuration.observe(viewLifecycleOwner, Observer { duration ->
            VwatchedDuration = duration
            Log.d("SubjectContentFragment", "Received duration: $duration")
            // Update UI or perform actions based on the received duration
        })

        Log.e("folderNames", folderNames.toString())
         if (folderIds.isNotEmpty()) {
             binding.tvTopicType.text = folderNames[0]
             folderProgress(folderIds[0])
         }
        // Set up layout manager for RecyclerView (before setting the adapter)
        binding.rvSubjectContent.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        // Generate the list of SubjectContentItem
        val subjectContentList = folderNames.mapIndexed { index, name ->
            Log.e("folderContentLog", name)
            val id = folderIds[index]
            SubjectContentItem(
                id = id,
                chapterNumber = index + 1,
                topicName = name,
                topicDescription = "Description for $name",
                locktime = ""
            )

        }


        binding.clTopicType.setOnClickListener {
            val bundle = Bundle().apply {
                putStringArrayList("folder_ids",  ArrayList(folderIds))
                putStringArrayList("folder_names",  ArrayList(folderNames))
                putString("folder_Count",folder_Count)
            }
            Log.e("clickevent",folderIds.toString())
            val bottomSheet = BottomsheetCourseTopicTypeFragment().apply { arguments = bundle
            }
            bottomSheet.setOnTopicTypeSelectedListener(object : OnTopicTypeSelectedListener {
                override fun onTopicTypeSelected(selectedTopic: TopicTypeModel) {
                    binding.tvTopicType.text = selectedTopic.title

                    folderProgress(selectedTopic.id)
                }
            })
            bottomSheet.show(childFragmentManager, "BottomsheetCourseTopicTypeFragment")


        }
        binding.mtCount.text = "${folderNames.size} Chapters"
        binding.tvSubjectName.text = folderName?:""
//            binding.rvSubjectContent.adapter = SubjectContentAdapter(subjectContentList) { selectedItem ->
//
//
//        }
    }


    private fun folderProgress(folderId:String){
        Log.e("folderProgress",folderId)
        val free = arguments?.getBoolean("free")
        if (folderId != null) {
            // Trigger the API call
            coursesViewModel.findCourseFolderProgress(folderId)
        }
        coursesViewModel.courseFolderProgress.observe(viewLifecycleOwner) { result ->
            result.onSuccess { data ->
                Log.e("GetFolderdata", data.toString())
                val folderProgressFolder = data.findCourseFolderProgress.folder
                val folderProgressContent = data.findCourseFolderProgress.folderContents
                val subfolderDurationFolders = data.findCourseFolderProgress.subfolderDurations
                Log.e("subFolderdata", subfolderDurationFolders.toString())

                if (folderProgressFolder != null) {
                    if (!folderProgressContent.isNullOrEmpty()){
                        val subjectContentList = folderProgressContent?.mapIndexed { index, contents ->
                            Log.e("folderContentLog", contents.content?.file_url.toString())
                            val id = contents.content?.id?:""
                            val name = contents.content?.file_name?:""
                            TopicContentModel(
                                subjectIcon = if (contents.content?.file_type?.name == "PDF") {R.drawable.content_bg}else {R.drawable.group_1707478994},
                                id=id,
                                playIcon =if (contents.content?.file_type?.name == "VIDEO") R.drawable.play_video_icon else 0,
                                lecture =  "Lecture",
                                lecturerName = "Ashok",
                                topicName= name,
                                topicDescription= contents.content?.description.toString(),
                                progress = 1,
                                url =  contents.content?.file_url.toString(),
                                fileType = contents.content?.file_type?.name?:""
                            )


                        }?: emptyList()
                        val bundle = Bundle().apply {
                            putString(" folder_Id", folderId)
//                                putString(" folder_Name", folderName)
//                                putString(" folder_Count", folderCount)
                        }

                        binding.rvSubjectContent.adapter = TopicContentAdapter(subjectContentList,folderId) { topicContent, folderContentId ->

                            if (topicContent.fileType == "VIDEO")
                            {
                                // Handle the click event for video file typ
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
                    }

                    if (!subfolderDurationFolders.isNullOrEmpty()){
                        Log.e("subfolderDurationszs", subfolderDurationFolders.toString())
                        val folderIds = ArrayList(subfolderDurationFolders?.mapNotNull { it.folder?.id } ?: emptyList())
                        val folderNames = ArrayList(subfolderDurationFolders?.mapNotNull { it.folder?.name } ?: emptyList())
                        val folderCount = ArrayList(subfolderDurationFolders?.mapNotNull { it.folder?.folder_count?:0 } ?: emptyList())
                        val scheduled_time = ArrayList(subfolderDurationFolders?.mapNotNull { it.folder?.scheduled_time?:0 } ?: emptyList())

                        val bundle = Bundle().apply {
                            putStringArray("folder_ids", folderIds.toTypedArray())
                            putStringArray("folder_names",folderNames.toTypedArray())
                        }
                        binding.tvContentCount.text = "($folderCount)"
                        val subjectContentList = folderNames.mapIndexed { index, name ->
                            Log.e("folderContentLog", name)
                            val id = folderIds[index]
                            val date = scheduled_time[index].toString()

                            val time = helperFunctions.formatCourseDate(date)
                            SubjectContentItem(
                                id = id,
                                chapterNumber = index + 1,
                                topicName = name,
                                topicDescription = "Description for $name",
                                locktime = time
                            )

                        }

                        binding.rvSubjectContent.adapter = SubjectContentAdapter(subjectContentList) { selectedItem ->

                            videoProgress(selectedItem.id, currentDuration = VwatchedDuration)
                        }

                    }
                    else {
                        if (subfolderDurationFolders.isNullOrEmpty()) {
                            Log.e("folderContentsss", data.findCourseFolderProgress.folderContents.toString())
                               var folderContent = data.findCourseFolderProgress.folderContents
                            binding.tvContentCount.text = "(${folderContent?.size?:0})"
                            val subjectContentList = folderContent?.mapIndexed { index, contents ->
                                Log.e("folderContentLog", contents.content?.file_url.toString())
                                val id = contents.content?.id?:""
                                val name = contents.content?.file_name?:""
                                 TopicContentModel(
                                     subjectIcon = if (contents.content?.file_type?.name == "PDF") {R.drawable.content_bg}else {R.drawable.group_1707478994},
                                     id=id,
                                     playIcon =if (contents.content?.file_type?.name == "VIDEO") R.drawable.play_video_icon else 0,
                                     lecture =  "Lecture",
                                     lecturerName = "Ashok",
                                     topicName= name,
                                     topicDescription= contents.content?.description.toString(),
                                     progress = 1,
                                     url =  contents.content?.file_url.toString(),
                                     fileType = contents.content?.file_type.toString()
                                )


                            }?: emptyList()
                            val bundle = Bundle().apply {
                                putString(" folder_Id", folderId)
//                                putString(" folder_Name", folderName)
//                                putString(" folder_Count", folderCount)
                            }

                            binding.rvSubjectContent.adapter = TopicContentAdapter(subjectContentList,folderId) { topicContent, folderContentId ->

                                if (topicContent.fileType == "VIDEO")
                                {
                                    // Handle the click event for video file typ
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
                        }
                    }
                }
            }.onFailure { error ->
                // Handle the error
                Log.e("AllDemoResourcesFree", error.message.toString())
            }
        }
    }

    fun videoProgress(courseFolderContentId:String,currentDuration:Int) {


        // Observe the result of the updateVideoProgress mutation
        videourlViewModel.updateVideoProgressResult.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(
                    requireContext(),
                    "Video progress updated successfully",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Failed to update video progress",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Call the mutation
        videourlViewModel.updateVideoProgress(courseFolderContentId, currentDuration)

    }

    fun videoUrlApi(viewModel: VideourlViewModel, folderContentId:String){

        viewModel.fetchVideoStreamUrl(folderContentId, "360p")

        viewModel.videoStreamUrl.observe(viewLifecycleOwner, { signedUrl ->
            Log.d("Videourl", "Signed URL: $signedUrl")
            if (signedUrl != null) {
                val bundle = Bundle().apply {
                    putString("url", signedUrl)
                    putString("ContentId", folderContentId)
                }
                findNavController().navigate(R.id.mediaFragment,bundle)

            } else {
                // Handle error or null URL
            }
        })
    }



}
