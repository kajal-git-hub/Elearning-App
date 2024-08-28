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
    private val folderId: String? by lazy {
        arguments?.getString("folder_Id")
    }


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
        val courseName  =  arguments?.getString("courseName")
        binding.courseNameResumeCourse.text = courseName
        folderId?.let { folderProgress(it) }
        Log.d("resumecourse name $courseName", "courseId: $folderId")
        binding.backIcon.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
        binding.clResumeCourseIcon2.setOnClickListener {
            findNavController().navigate(R.id.action_resumeCourseFragment_to_ScheduleFragment)
        }
        myCourse()
    }

    private fun myCourse() {
        myCourseViewModel.myCourses.observe(viewLifecycleOwner) { result ->
            result.onSuccess { data ->
               Log.e("datamyco",data.myCourses.toString())
                data.myCourses.forEach { courselist ->
                    Log.e("courseID", courselist.course.id)
                }
                if (data.myCourses.isNotEmpty()) {
                    dataBind(data.myCourses.get(0).progress?.subfolderDurations)

                }


                val course = data.myCourses.find { it.course.id == folderId }
                course?.let {
                    populateCourseData(it)
                } ?: run {
                    Toast.makeText(requireContext(), "Course not found", Toast.LENGTH_SHORT).show()
                }
            }.onFailure {
                Log.e("MyCoursesFail", it.message.toString())
                Toast.makeText(requireContext(), "Failed to load courses: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }

        myCourseViewModel.fetchMyCourses()
    }

    private fun populateCourseData(course: MyCoursesQuery.MyCourse) {
        binding.courseNameResumeCourse.text = course.course.name
    }

    fun folderProgress(folderId:String){
        if (folderId != null) {
            coursesViewModel.findCourseFolderProgress(folderId)
        }
        coursesViewModel.courseFolderProgress.observe(viewLifecycleOwner) { result ->
            result.onSuccess { data ->
                Log.e("GetFolderdata", data.findCourseFolderProgress.folder.toString())
                val subfolderDurations = data.findCourseFolderProgress.subfolderDurations
                Log.e("subfolderDurations", subfolderDurations.toString())
                val courseSetOnClickListener = listOf(
                    binding.clMathematics,
                    binding.clChemistry,
                    binding.clPhysics,
                    binding.clSM,
                    binding.clClassNotes
                )
                courseSetOnClickListener.forEach { view ->
                    subfolderDurations?.forEach { folders ->
                        setupNavigation(view, folders.folder)
                    }
                }


            }.onFailure { error ->
                Log.e("AllDemoResourcesFree", error.message.toString())
            }
        }
    }

    fun dataBind(subfolderlist: List<MyCoursesQuery.SubfolderDuration>?) {
        if (subfolderlist != null) {


            val progressIndicators = listOf(
                binding.customProgressIndicatorMathematics,
                binding.customProgressIndicatorChemistry,
                binding.customProgressIndicatorPhysics,
                binding.customProgressIndicatorSM,
                binding.customProgressIndicatorClassNotes
            )

            val textViews = listOf(
                binding.tvMathematics,
                binding.tvChemistry,
                binding.tvPhysics,
                binding.tvSM,
                binding.tvClassNotes
            )
            val textPercentage = listOf(
                binding.tvPercentCompletedMathematics,
                binding.tvPercentCompletedChemistry,
                binding.tvPercentCompletedPhysics,
                binding.tvPercentCompletedSM,
                binding.tvPercentCompletedClassNotes
            )

            val textchapterCount = listOf(
                binding.tvNoOfChaptersMathematics,
                binding.tvNoOfChaptersChemistry,
                binding.tvNoOfChaptersPhysics
            )

            val subfolderDurations = subfolderlist[0].folder

            progressIndicators.forEachIndexed { index, progressIndicator ->
                val completionPercentage =
                    subfolderlist.getOrNull(index)?.completionPercentage?.toInt() ?: 0
                progressIndicator.progress = completionPercentage
            }

            textViews.forEachIndexed { index, textView ->
                val folderName = subfolderlist.getOrNull(index)?.folder?.name ?: ""
                Log.e("foldernam", folderName)
                textView.text = folderName
            }

            textchapterCount.forEachIndexed { index, textchapterCount ->
                val foldersize = subfolderlist.size
                Log.e("foldernam", foldersize.toString())
                textchapterCount.text = "$foldersize Chapters"
            }

            textPercentage.forEachIndexed { index, textPercentage ->
                val completionPercentage =
                    subfolderlist.getOrNull(index)?.completionPercentage?.toInt() ?: 0
                textPercentage.text = "$completionPercentage%"
            }

            progressIndicators.forEachIndexed { index, progressIndicator ->
                val completionPercentage =
                    subfolderlist?.getOrNull(index)?.completionPercentage?.toInt() ?: 0
                progressIndicator.progress = completionPercentage

            }
        }

    }

    private fun setupNavigation(view: View, subFolder: FindCourseFolderProgressQuery.Folder1?) {
        view.setOnClickListener {
           if (subFolder != null) {
                findNavController().navigate(R.id.TopicTYPEContentFragment)
            } else {
                findNavController().navigate(R.id.action_resumeCourseFragment_to_subjectContentFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
