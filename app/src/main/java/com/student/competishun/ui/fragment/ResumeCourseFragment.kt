package com.student.competishun.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.student.competishun.R
import com.student.competishun.curator.MyCoursesQuery
import com.student.competishun.databinding.FragmentResumeCourseBinding
import com.student.competishun.ui.main.HomeActivity
import com.student.competishun.ui.viewmodel.CoursesViewModel
import com.student.competishun.ui.viewmodel.MyCoursesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResumeCourseFragment : Fragment() {

    private var _binding: FragmentResumeCourseBinding? = null
    private val binding get() = _binding!!
    private val myCourseViewModel: MyCoursesViewModel by viewModels()
    private val coursesViewModel: CoursesViewModel by viewModels()
    var folderNames:ArrayList<String>? = null
    var folderIds: ArrayList<String>? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentResumeCourseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        (activity as? HomeActivity)?.showBottomNavigationView(false)
        (activity as? HomeActivity)?.showFloatingButton(false)

        binding.backIcon.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        folderIds = arguments?.getStringArrayList("folder_ids")
         folderNames = arguments?.getStringArrayList("folder_names")
        var completionPercentage = arguments?.getDouble("subfolderDurations")?:0.0
        var folderCounts = arguments?.getStringArrayList("folderCounts")
        val courseName  =  arguments?.getString("courseName")

        binding.courseNameResumeCourse.text = courseName
        Log.d("resumename $courseName", "folderIds: $folderIds")
        Log.e("ffoldername $folderNames", "folders: $folderIds")
        Log.d("resumecourse name $courseName", "courseId: $folderIds")
        binding.backIcon.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
        binding.clResumeCourseIcon2.setOnClickListener {
            findNavController().navigate(R.id.action_resumeCourseFragment_to_ScheduleFragment)
        }
        dataBind(folderNames,folderIds,completionPercentage,folderCounts)

    }



    private fun populateCourseData(course: MyCoursesQuery.MyCourse) {
        binding.courseNameResumeCourse.text = course.course.name
    }

    private fun folderProgress(folderId: String, folderNames: String,folderCount: String){


        Log.e("folderProgresss",folderId)
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
                var Name = folderNames


                if (folderProgressFolder != null) {

                    if (!subfolderDurationFolders.isNullOrEmpty()){
                        Log.e("subfolderDurationszs", subfolderDurationFolders.toString())
                        val folderIds = ArrayList(subfolderDurationFolders.mapNotNull { it.folder?.id } ?: emptyList())
                        val folderNames = ArrayList(subfolderDurationFolders.mapNotNull { it.folder?.name } ?: emptyList())
                        Log.e("folderNamesss", folderNames.toString())
                        val bundle = Bundle().apply {
                            putStringArrayList("folder_ids",  ArrayList(folderIds))
                            putStringArrayList("folder_names", ArrayList(folderNames))
                            putString("folder_Name",Name)
                        }
                        findNavController().navigate(R.id.SubjectContentFragment,bundle)
                    }
                    else {
                        if (subfolderDurationFolders.isNullOrEmpty()) {
                            Log.e("folderContentsss", data.findCourseFolderProgress.folderContents.toString())

                            val bundle = Bundle().apply {
                                putString("folder_Id", folderId)
                                putString("folder_Name", folderNames)
                                putString("folder_Count", folderCount)
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


    private fun dataBind(
        folderNames: ArrayList<String>?,
        folderIds: ArrayList<String>?,
        completionPercentage: Double,
        folderCount: ArrayList<String>?
    ) {

        if (folderIds != null && folderNames !=null) {


            val textViews = listOf(
                binding.tvMathematics,
                binding.tvChemistry,
                binding.tvPhysics,
                binding.tvSM,
                binding.tvClassNotes
            )
            textViews.forEachIndexed { index, textView ->
                val folderName = folderNames?.getOrNull(index) ?: "NA"
                Log.e("foldernam", folderName)
                textView.text = folderName
            }
            val textchapterCount = listOf(
                binding.tvNoOfChaptersMathematics,
                binding.tvNoOfChaptersChemistry,
                binding.tvNoOfChaptersPhysics,
            )

            val textPercentage = listOf(
                binding.tvPercentCompletedMathematics,
                binding.tvPercentCompletedChemistry,
                binding.tvPercentCompletedPhysics,
                binding.tvPercentCompletedSM,
                binding.tvPercentCompletedClassNotes
            )
            val courseSetOnClickListener = listOf(
                binding.clMathematics,
                binding.clChemistry,
                binding.clPhysics,
                binding.clSM,
                binding.clClassNotes
            )


            val progressIndicators = listOf(
                binding.customProgressIndicatorMathematics,
                binding.customProgressIndicatorChemistry,
                binding.customProgressIndicatorPhysics,
                binding.customProgressIndicatorSM,
                binding.customProgressIndicatorClassNotes
            )

            textchapterCount.forEachIndexed { index, textchapterCount ->
                val foldersize = folderCount?.getOrNull(index)
                Log.e("foldernam", foldersize.toString())
                textchapterCount.text = "$foldersize Chapters"
            }

            progressIndicators.forEachIndexed { index, progressIndicator ->
                val completionPercentage = completionPercentage
                progressIndicator.progress = completionPercentage?.toInt()?:0

            }

            courseSetOnClickListener.forEachIndexed { index, view ->
                view.setOnClickListener {
                    Log.e("Clickedon", view.toString())
                    val folderId = folderIds.getOrNull(index)
                    val folderName = folderNames.getOrNull(index)?:""
                    val folderCount =  folderCount?.getOrNull(index)?:"0"
                    if (folderId != null) {
                        Log.e("subfolderDuration", folderId.toString())
                        folderProgress(folderId,folderName,folderCount)
                    } else {
                        Log.e("Error", "No folderId found for this view")
                    }
                }
            }


            textPercentage.forEachIndexed { index, textPercentage ->
                val completionPercentage = String.format("%.2f", completionPercentage)
                textPercentage.text = "$completionPercentage%"
            }



        }

    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

