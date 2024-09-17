package com.student.competishun.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.student.competishun.R
import com.student.competishun.curator.FindCourseParentFolderProgressQuery
import com.student.competishun.curator.GetAllCourseCategoriesQuery
import com.student.competishun.curator.MyCoursesQuery
import com.student.competishun.databinding.FragmentResumeCourseBinding
import com.student.competishun.ui.adapter.ExploreCourseAdapter
import com.student.competishun.ui.adapter.OurCoursesAdapter
import com.student.competishun.ui.adapter.OurSubjectsAdapter
import com.student.competishun.ui.main.HomeActivity
import com.student.competishun.ui.viewmodel.CoursesViewModel
import com.student.competishun.ui.viewmodel.MyCoursesViewModel
import com.student.competishun.utils.OnCourseItemClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResumeCourseFragment : Fragment() {

    private lateinit var binding: FragmentResumeCourseBinding
    private val myCourseViewModel: MyCoursesViewModel by viewModels()
    private val coursesViewModel: CoursesViewModel by viewModels()
    var folderNames:ArrayList<String>? = null
    var folderIds: String? = null
    val gson = Gson()
    private lateinit var adapterOurSubjectsAdapter: OurSubjectsAdapter
    private lateinit var rvOurSubjects: RecyclerView
    private lateinit var listOuSubjectItem: List<FindCourseParentFolderProgressQuery.Folder?>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentResumeCourseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backIcon.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.clResumeCourseIcon3.setOnClickListener {
            findNavController().navigate(R.id.DownloadFragment)
        }

        (activity as? HomeActivity)?.showBottomNavigationView(false)
        (activity as? HomeActivity)?.showFloatingButton(false)

        val completionPercentagesArray = arguments?.getDoubleArray("completionPercentages")
        val completionPercentages = completionPercentagesArray?.toList() ?: emptyList()
        Log.e("completionper $completionPercentages",completionPercentagesArray.toString())

        var folderCounts = arguments?.getString("completionPercentages")
        val courses =  arguments?.getString("courseJson")
        val coursesName =  arguments?.getString("courseName")
        val folders = arguments?.getString("folderJson")
        val CourseId = arguments?.getString("courseId")
        val courseStart =  arguments?.getString("courseStart")
        val CourseEnd =  arguments?.getString("courseEnd")

        binding.courseNameResumeCourse.text = coursesName
        binding.backIcon.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
        val bundle = Bundle().apply {
            putString("courseId", CourseId)
            putString("courseStart",courseStart)
            putString("courseEnd",CourseEnd)
        }

        if (CourseId!=null) {
            parentFolderProgress(CourseId)
        }
        if (folders!=null && courses !=null && folderCounts!=null){

       // dataBind(folders,courses,folderCounts)
        }
        rvOurSubjects = view.findViewById(R.id.rvOurSubject)
        binding.clResumeCourseIcon2.setOnClickListener {
            findNavController().navigate(R.id.action_resumeCourseFragment_to_ScheduleFragment,bundle)
        }
       // dataBind(folderIds,courseName!!,folderCounts!!)

    }



    private fun populateCourseData(course: MyCoursesQuery.MyCourse) {
        binding.courseNameResumeCourse.text = course.course.name
    }

    private fun parentFolderProgress(courseId:String){
        coursesViewModel.findCourseParentFolderProgress(courseId)
        coursesViewModel.courseParentFolderProgress.observe(viewLifecycleOwner){ result ->
            when (result) {
                is com.student.competishun.di.Result.Success -> {
                    val subfolderProgress = result.data.findCourseParentFolderProgress.subfolderProgress
                    if (subfolderProgress.isNullOrEmpty()){
                        Log.e("subfolderProgress",subfolderProgress.toString())
                    }else{
                        val folders = subfolderProgress.map { parentFolderItem ->
                            parentFolderItem.folder
                        }
                        val percentages = subfolderProgress.map { parentFolderItem ->
                            parentFolderItem.completionPercentage
                        }
                        listOuSubjectItem = folders
                        val adapter = OurSubjectsAdapter(listOuSubjectItem, percentages) {folderId,foldername,folderCount ->
                            Log.e("foldersz",folderId+foldername.toString())
                            // Handle the course click and navigate
                            folderProgress(folderId,foldername,folderCount)

                        }

                        binding.rvOurSubject.adapter = adapter
                        binding.rvOurSubject.layoutManager =
                            GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)

                    }
                }
                is com.student.competishun.di.Result.Failure -> {}
                is com.student.competishun.di.Result.Loading -> {}
            }
        }
    }

    private fun folderProgress(folderId: String, folderNames: String,folderCount: String){

        Log.e("foldessss $folderNames",folderId)
        val free = arguments?.getBoolean("free")
        if (folderId != null) {
            // Trigger the API call
            coursesViewModel.findCourseFolderProgress(folderId)
        }
        coursesViewModel.courseFolderProgress.observe(viewLifecycleOwner) { result ->
            when (result) {
                is com.student.competishun.di.Result.Success -> {
                    val data = result.data
                    Log.e("GetFolderdata", data.toString())
                    Log.e("GetFolders", data.findCourseFolderProgress.folder.toString())

                    val folderProgressFolder = data.findCourseFolderProgress.folder
                    val folderProgressContent = data.findCourseFolderProgress.folderContents
                    val subfolderDurationFolders = data.findCourseFolderProgress.subfolderDurations
                    Log.e("subFolderdata", subfolderDurationFolders.toString())
                    val name = folderNames // Ensure folderNames is correctly assigned before using

                    if (folderProgressFolder != null) {
                        if (!subfolderDurationFolders.isNullOrEmpty()) {
                            // Process subfolder durations
                            Log.e("subfolderDurationszs", subfolderDurationFolders.toString())

                            val gson = Gson()
                            val subFoldersJson = gson.toJson(subfolderDurationFolders)
                            Log.e("folderNamesss $name", subFoldersJson)

                            val bundle = Bundle().apply {
                                putString("subFolders", subFoldersJson)
                                putString("folder_Name", name)
                            }
                            findNavController().navigate(R.id.SubjectContentFragment, bundle)
                        } else if (!folderProgressContent.isNullOrEmpty()) {
                            // Process folder contents
                            Log.e("folderContentsss", folderProgressContent.toString())

                            val gson = Gson()
                            val folderContentsJson = gson.toJson(folderProgressContent)

                            val bundle = Bundle().apply {

                                putString("folderContents", folderContentsJson)
                                putString("folder_Id", folderId)
                                putString("folderName", folderNames)

                            }
                            findNavController().navigate(R.id.TopicTYPEContentFragment, bundle)
                        }
                    }
                }
                is com.student.competishun.di.Result.Failure -> {
                    // Handle errors
                    Log.e("AllDemoResourcesFree", result.exception.message.toString())
                }
                is com.student.competishun.di.Result.Loading -> {
                    // Optionally handle loading state
                    Log.e("LoadingState", "Data is loading...")
                }
            }
        }


    }



    private fun dataBind(
        folderss: String,
        course:String,
        progressPerc: String
    ) {
        val folderListType = object : TypeToken<List<MyCoursesQuery.Folder1>>() {}.type
        val folders: List<MyCoursesQuery.Folder1> = gson.fromJson(folderss, folderListType)

        val percentageListType = object : TypeToken<List<Double>>() {}.type
        val percentage: List<MyCoursesQuery.SubfolderDuration> = gson.fromJson(progressPerc, percentageListType)


        if (folders != null && course !=null) {
           // listOuSubjectItem = folders
           // var progress = parseFolders(progressPerc)

//            val adapter = OurSubjectsAdapter(listOuSubjectItem, percentage) {folderId,foldername,folderCount ->
//                Log.e("foldersz",folderId+foldername.toString())
//                // Handle the course click and navigate
//               folderProgress(folderId,foldername,folderCount)
//
//            }

          //  binding.rvOurSubject.adapter = adapter
            binding.rvOurSubject.layoutManager =
                GridLayoutManager(context, 3, GridLayoutManager.HORIZONTAL, false)


        }

    }

}

