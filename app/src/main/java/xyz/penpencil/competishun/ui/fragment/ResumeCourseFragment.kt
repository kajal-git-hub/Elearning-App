package xyz.penpencil.competishun.ui.fragment

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.student.competishun.curator.FindCourseParentFolderProgressQuery
import com.student.competishun.curator.MyCoursesQuery
import xyz.penpencil.competishun.ui.adapter.OurSubjectsAdapter
import xyz.penpencil.competishun.ui.main.HomeActivity
import dagger.hilt.android.AndroidEntryPoint
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.databinding.FragmentResumeCourseBinding
import xyz.penpencil.competishun.di.Result
import xyz.penpencil.competishun.ui.viewmodel.ResumeCoursesViewModel
import xyz.penpencil.competishun.utils.setLightStatusBars

@AndroidEntryPoint
class ResumeCourseFragment : DrawerVisibility() {

    private lateinit var binding: FragmentResumeCourseBinding
    private val coursesViewModel: ResumeCoursesViewModel by viewModels()
    val gson = Gson()
    private var rvOurSubjects: RecyclerView ?= null
    private var listOuSubjectItem: List<FindCourseParentFolderProgressQuery.Folder?> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResumeCourseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? HomeActivity)?.let {
            it.showBottomNavigationView(false)
            it.showFloatingButton(false)
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        })

        binding.courseNameResumeCourse.setOnClickListener {
            it.findNavController().popBackStack()
        }

        binding.clResumeCourseIcon4.setOnClickListener {
            findNavController().navigate(R.id.BookMarkFragment)
        }
        binding.clResumeCourseIcon3.setOnClickListener {
            findNavController().navigate(R.id.DownloadFragment)
        }

        val completionPercentagesArray = arguments?.getDoubleArray("completionPercentages")
        val folderCounts = arguments?.getString("completionPercentages")
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
    }



    private fun populateCourseData(course: MyCoursesQuery.MyCourse) {
        binding.courseNameResumeCourse.text = course.course.name
    }

    private fun parentFolderProgress(courseId:String){
        coursesViewModel.findCourseParentFolderProgress(courseId)
        binding.progressBar.visibility = View.VISIBLE
        binding.rvOurSubject.visibility = View.GONE
        coursesViewModel.courseParentFolderProgress.observe(viewLifecycleOwner){ result ->
            when (result) {
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.rvOurSubject.visibility = View.VISIBLE
                    val subfolderProgress = result.data.findCourseParentFolderProgress.subfolderProgress
                    Log.e("subfolderProgrss",subfolderProgress.toString())
                    if (subfolderProgress.isNullOrEmpty()){
                        Log.e("subfolderProgress", "")
                    }else{
                        val listOuSubjectItem = subfolderProgress.map { it.folder }
                        val percentages = subfolderProgress.map { it.completionPercentage }
                    //    freeBadgeImageView
                        binding.rvOurSubject.apply {
                            layoutManager = GridLayoutManager(context, 2)
                            adapter = OurSubjectsAdapter(listOuSubjectItem, percentages) { folderId, folderName, folderCount ->
                                folderProgress(folderId, folderName, folderCount)
                            }
                        }
                    }
                }
                is Result.Failure -> {}
                is Result.Loading -> {
                }
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
                is Result.Success -> {
                    val data = result.data
                    Log.e("GetFolderdata", data.toString())
                    Log.e("GetFolders", data.findCourseFolderProgress.folder.toString())

                    val folderProgressFolder = data.findCourseFolderProgress.folder
                    val folderProgressContent = data.findCourseFolderProgress.folderContents
                    val subfolderDurationFolders = data.findCourseFolderProgress.subfolderDurations
                    Log.e("subFolderdata", subfolderDurationFolders.toString())
                    val name = folderNames //  folderNames is correctly assigned before using

                    if (folderProgressFolder != null) {
                        if (subfolderDurationFolders?.isNotEmpty() == true) {
                            // Process subfolder durations
                            Log.e("resumefolderDurationszs", folderProgressContent.toString())

                            val gson = Gson()
                            val subFoldersJson = gson.toJson(subfolderDurationFolders)
                            val foldercontent =  gson.toJson(folderProgressContent)
                            Log.e("folderNamesss $name", subFoldersJson)

                            val bundle = Bundle().apply {
                                putString("subFolders", subFoldersJson)
                                putString("folder_Name", name)
                                putString("folder_Id", folderId)
                                putString("parent_content",foldercontent)
                                putBoolean("isExternal",name.contains("DPP", ignoreCase = true))
                            }
                            findNavController().navigate(R.id.SubjectContentFragment, bundle)
                        } else if (folderProgressContent?.isNotEmpty() == true) {
                            val gson = Gson()
                            val folderContentsJson = gson.toJson(folderProgressContent)
                            val bundle = Bundle().apply {
                                putString("folderContents", folderContentsJson)
                                putString("folder_Id", folderId)
                                putString("folderName", folderNames)
                                putBoolean("isExternal",name.contains("DPP", ignoreCase = true))
                            }
                            findNavController().navigate(R.id.TopicTYPEContentFragment, bundle)
                        }else{
                            Toast.makeText(requireContext(), "No Content", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(requireContext(), "No Content", Toast.LENGTH_SHORT).show()
                    }
                }
                is Result.Failure -> {
                    // Handle errors
                    Log.e("AllDemoResourcesFree", result.exception.message.toString())
                }
                is Result.Loading -> {
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

    override fun onResume() {
        super.onResume()
        setStatusBarGradiant(requireActivity())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
        requireActivity().window.setBackgroundDrawable(null)
    }

    private fun setStatusBarGradiant(activity: Activity) {
        val window: Window = activity.window
        val background = ContextCompat.getDrawable(activity, R.drawable.gradiant_bg)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(activity,android.R.color.transparent)
        window.setLightStatusBars(true)
        window.setBackgroundDrawable(background)
    }

}

