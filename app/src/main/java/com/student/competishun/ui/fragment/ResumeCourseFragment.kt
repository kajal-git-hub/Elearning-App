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
import com.student.competishun.curator.FindCourseFolderProgressQuery
import com.student.competishun.curator.MyCoursesQuery
import com.student.competishun.databinding.FragmentResumeCourseBinding
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
        binding.backIcon.setOnClickListener {
            requireActivity().onBackPressed()
        }
        folderIds = arguments?.getStringArrayList("folder_ids")
         folderNames = arguments?.getStringArrayList("folder_names")
        var progress = arguments?.getDouble("progress")?:0.0
        val courseName  =  arguments?.getString("courseName")

        binding.courseNameResumeCourse.text = courseName
        Log.d("resumename $courseName", "folderIds: $folderIds")
        Log.e("ffoldername $folderNames", "folders: $folderIds")
        Log.d("resumecourse name $courseName", "courseId: $folderIds")
        binding.backIcon.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
        binding.clResumeCourseIcon2.setOnClickListener {
            findNavController().navigate(R.id.action_resumeCourseFragment_to_ScheduleFragment)
        }
        dataBind(folderNames,folderIds,progress)
        showShimmer()
        myCourse()
    }

    private fun myCourse() {
            myCourseViewModel.myCourses.observe(viewLifecycleOwner) { result ->
                result.onSuccess { data ->
                    Log.e("datamyco", data.myCourses.toString())
                    data.myCourses.forEach { courselist ->
                        Log.e("courseID", courselist.course.id)
                    }
                    if (data.myCourses.isNotEmpty()) {
                        hideShimmer()
                       // var foldrduration =
                      //  dataBind(data.myCourses.get(0).progress?.subfolderDurations)

                    }


//                    val course = data.myCourses.find { it.course.id == folderId }
//                    course?.let {
 //                       populateCourseData(it)
//                    } ?: run {
//                       Log.e( "Course not found", course?.course?.folder.toString())
//                    }
                }.onFailure {
                    Log.e("MyCoursesFail", it.message.toString())
                    Toast.makeText(
                        requireContext(),
                        "Failed to load courses: ${it.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

        }
        myCourseViewModel.fetchMyCourses()

    }

    private fun populateCourseData(course: MyCoursesQuery.MyCourse) {
        binding.courseNameResumeCourse.text = course.course.name
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

                    if (!subfolderDurationFolders.isNullOrEmpty()){
                        Log.e("subfolderDurationszs", subfolderDurationFolders.toString())
                        findNavController().navigate(R.id.TopicTYPEContentFragment)
                    }
                    else {
                        if (subfolderDurationFolders.isNullOrEmpty()) {
                            Log.e("folderContentsss", data.findCourseFolderProgress.folderContents.toString())

                            findNavController().navigate(R.id.action_resumeCourseFragment_to_subjectContentFragment)

                        }
                    }
                }
            }.onFailure { error ->
                // Handle the error
                Log.e("AllDemoResourcesFree", error.message.toString())
            }
        }
    }

    private fun dataBind(folderNames: ArrayList<String>?, folderIds: ArrayList<String>?,completionPercentage:Double) {

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

            val textchapterCount = listOf(
                binding.tvNoOfChaptersMathematics,
                binding.tvNoOfChaptersChemistry,
                binding.tvNoOfChaptersPhysics
            )

            val progressIndicators = listOf(
                binding.customProgressIndicatorMathematics,
                binding.customProgressIndicatorChemistry,
                binding.customProgressIndicatorPhysics,
                binding.customProgressIndicatorSM,
                binding.customProgressIndicatorClassNotes
            )

            textchapterCount.forEachIndexed { index, textchapterCount ->
                val foldersize = 0
                Log.e("foldernam", foldersize.toString())
                textchapterCount.text = "$foldersize Chapters"
            }

            progressIndicators.forEachIndexed { index, progressIndicator ->
                val completionPercentage = completionPercentage
                progressIndicator.progress = completionPercentage?.toInt()?:0

            }

            courseSetOnClickListener.forEach { view ->
                Log.e("clickded on ",view.toString())
                folderIds?.forEach { Ids ->
                    Log.e("subfolderDurations on ",Ids.toString())

                    setupNavigation(view, Ids)
                }

            }


            textPercentage.forEachIndexed { index, textPercentage ->
                val completionPercentage = String.format("%.2f", completionPercentage)
                textPercentage.text = "$completionPercentage%"
            }



        }

    }

    private fun setupNavigation(view: View, subFolder: String) {
        Log.e("subfoleer",subFolder.toString())

        view.setOnClickListener {

           if (subFolder != null) {
               folderProgress(subFolder)
           }
        }
    }

    private fun showShimmer() {
        binding.progress.visibility = View.VISIBLE
        binding.clSubjectsAndTopics.visibility = View.GONE
    }

    private fun hideShimmer() {
        binding. progress.visibility = View.GONE
        binding.clSubjectsAndTopics.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

