package com.student.competishun.ui.fragment

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.student.competishun.R
import com.student.competishun.data.model.SubjectContentItem
import com.student.competishun.data.model.TopicTypeModel
import com.student.competishun.databinding.FragmentBottomsheetCourseTopicTypeBinding
import com.student.competishun.ui.adapter.TopicTypeAdapter
import com.student.competishun.ui.viewmodel.CoursesViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.student.competishun.ui.main.HomeActivity
import com.student.competishun.utils.OnTopicTypeSelectedListener

@AndroidEntryPoint
class BottomsheetCourseTopicTypeFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentBottomsheetCourseTopicTypeBinding
    private lateinit var topicTypeAdapter: TopicTypeAdapter
    private val coursesViewModel: CoursesViewModel by viewModels()
    private var listener: OnTopicTypeSelectedListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomsheetCourseTopicTypeBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? HomeActivity)?.showBottomNavigationView(false)
        (activity as? HomeActivity)?.showFloatingButton(false)

        val folderIds = arguments?.getStringArrayList("folder_ids")?: arrayListOf()
        val folderNames = arguments?.getStringArrayList("folder_names")?: arrayListOf()
        val folderCount = arguments?.getString("folder_Count")?:"0"
        // Create a list of TopicTypeModel using folderNames
        val topicTypeList = folderIds.mapIndexed { index, id ->
            val name = folderNames.getOrNull(index) ?: "Unknown Name" // Handle out-of-range case
            TopicTypeModel(id = id, title = name, count = folderCount)
        }
        binding.tvTitleNumber.text = "($folderCount)"



        // Initialize the adapter and set it to the RecyclerView
        topicTypeAdapter = TopicTypeAdapter(topicTypeList) { selectedTopic ->
            listener?.onTopicTypeSelected(selectedTopic)
            dismiss()
        }
        binding.rvTopicTypes.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = topicTypeAdapter
        }
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
                        subfolderDurationFolders.forEach { folders ->
                            folders.folder?.id
                            folders.folder?.name
                            folders.folder?.folder_count
                        }
                        val topicTypeList = subfolderDurationFolders?.map { folder ->
                            TopicTypeModel(
                                title = "${folder.folder?.name} (${folder.folder?.folder_count ?: 0})",
                                count = "",id=""
                            )
                        } ?: emptyList()

                        // Set up the adapter with the newly created topicTypeList
                       // val topicTypeAdapter = TopicTypeAdapter(topicTypeList)

                        // Update RecyclerView with the new list
//                        binding.rvTopicTypes.apply {
//                            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
//                            adapter = topicTypeAdapter
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

    fun setOnTopicTypeSelectedListener(listener: OnTopicTypeSelectedListener) {
        this.listener = listener
    }


    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listener = null
    }

}