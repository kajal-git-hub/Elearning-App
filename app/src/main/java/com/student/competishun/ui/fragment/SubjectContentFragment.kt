package com.student.competishun.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.student.competishun.R
import com.student.competishun.curator.FindCourseFolderProgressQuery
import com.student.competishun.curator.type.FileType
import com.student.competishun.data.model.FreeDemoItem
import com.student.competishun.data.model.SubjectContentItem
import com.student.competishun.databinding.FragmentSubjectContentBinding
import com.student.competishun.ui.adapter.SubjectContentAdapter
import com.student.competishun.ui.viewmodel.CoursesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SubjectContentFragment : Fragment() {

    private var _binding: FragmentSubjectContentBinding? = null
    private val binding get() = _binding!!
    private val coursesViewModel: CoursesViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSubjectContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backIconSubjectContent.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Retrieve arguments passed to the fragment
        val folderIds = arguments?.getStringArrayList("folder_ids") ?: arrayListOf()
        val folderNames = arguments?.getStringArrayList("folder_names") ?: arrayListOf()
        val courseName = arguments?.getString("courseName") ?: ""
        val folderName = arguments?.getString("folder_Name") ?: ""
        var folder_Count = arguments?.getString("folder_Count")?:"0"

        Log.e("folderNames", folderNames.toString())

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
            )

        }
        val bundle = Bundle().apply {
            putStringArrayList("folder_ids",  ArrayList(folderIds))
            putStringArrayList("folder_names",  ArrayList(folderNames))
            putString("folder_Count",folder_Count)
        }
        binding.clTopicType.setOnClickListener {
            Log.e("clickevent",folderIds.toString())
            val bottomSheet = BottomsheetCourseTopicTypeFragment().apply { arguments = bundle
            }
            bottomSheet.show(
                childFragmentManager,
                "BottomsheetCourseTopicTypeFragment"
            )
        }
        binding.mtCount.text = "${folderNames.size} Chapters"
        binding.tvSubjectName.text = folderName?:""
            binding.rvSubjectContent.adapter = SubjectContentAdapter(subjectContentList) { selectedItem ->
                var folderId = selectedItem.id
                var folderName = selectedItem.topicName
                folderProgress(folderId,folderName,folder_Count)
                Log.d("SelectedItem", "Clicked on: ${selectedItem.topicName}")
//                val bundle = Bundle().apply {
//                    putStringArrayList("folder_ids", ArrayList(folderIds))
//                    putStringArrayList("folder_names", ArrayList(folderNames))
//                }


        }
        // Handle bottom sheet selection

    }


    private fun folderProgress(folderId:String,folderName: String,folderCount:String){
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

                    if (!subfolderDurationFolders.isNullOrEmpty()){
                        Log.e("subfolderDurationszs", subfolderDurationFolders.toString())
                        val folderIds = ArrayList(subfolderDurationFolders?.mapNotNull { it.folder?.id } ?: emptyList())
                        val folderNames = ArrayList(subfolderDurationFolders?.mapNotNull { it.folder?.name } ?: emptyList())
                        val bundle = Bundle().apply {
                            putStringArray("folder_ids", folderIds.toTypedArray())
                            putStringArray("folder_names",folderNames.toTypedArray())
                        }

//                        binding.downChooseTopic.setOnClickListener {
//                            val bottomSheet = BottomsheetCourseTopicTypeFragment().apply {
//                                arguments = bundle // Set the arguments to pass data to the fragment
//                            }
//                            bottomSheet.show(
//                                childFragmentManager,
//                                "BottomsheetCourseTopicTypeFragment"
//                            )
//                        }
                    }
                    else {
                        if (subfolderDurationFolders.isNullOrEmpty()) {
                            Log.e("folderContentsss", data.findCourseFolderProgress.folderContents.toString())
                               var folderContent = data.findCourseFolderProgress.folderContents?.forEach { it }
                            val bundle = Bundle().apply {
                                putString(" folder_Id", folderId)
                                putString(" folder_Name", folderName)
                                putString(" folder_Count", folderCount)
                            }
                            findNavController().navigate(R.id.TopicTYPEContentFragment,bundle)
                        }
                    }
                }
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
