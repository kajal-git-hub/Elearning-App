package xyz.penpencil.competishun.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.apollographql.apollo3.api.Optional
import com.student.competishun.curator.type.FindAllCourseInputStudent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import xyz.penpencil.competishun.databinding.FragmentYTCourseBinding
import xyz.penpencil.competishun.ui.adapter.StudyCoursesAdapter
import xyz.penpencil.competishun.ui.adapter.YTCourseAdapter
import xyz.penpencil.competishun.ui.viewmodel.StudentCoursesViewModel
import xyz.penpencil.competishun.utils.FilterSelectionListener
import xyz.penpencil.competishun.utils.HelperFunctions

@AndroidEntryPoint
class YTCourseFragment : Fragment(), FilterSelectionListener {
    private lateinit var helperFunctions: HelperFunctions
    private lateinit var binding: FragmentYTCourseBinding
    private val filterOptions = listOf("IIT-JEE", "NEET")
    private val courseViewModel: StudentCoursesViewModel by viewModels()
    private val TAG = "YTCourseFragment"
    private var autoSelectedExam = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentYTCourseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backIcon.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.tvFilterTextYT.setOnClickListener {
            val filterYTFragment = FilterStudyMaterialFragment()
            filterYTFragment.show(childFragmentManager, "FilterStudyMaterialFragment")
        }
       fetchCoursesForClass("","")
        setupToggleRecyclerView()
        binding.tvCourseMaterialCount.text = "0 Courses"
        binding.tvShowingResults.text = "Showing results (0):"

    }

    private fun setupToggleRecyclerView() {
        val adapter = StudyCoursesAdapter(filterOptions,autoSelectedExam) { selectedOption ->
            Log.d("SelectedOption", "Selected: $selectedOption")

            val filters = FindAllCourseInputStudent(
                category_name = Optional.present("Study Material"),
                exam_type = Optional.present(selectedOption)
            )
            courseViewModel.fetchCourses(filters)
            observeCourses()
        }
    }

    private fun fetchCoursesForClass(courseClass: String, selectedExam: String?) {
        val categoryName = arguments?.getString("category_name")
        val examType = arguments?.getString("exam_type")
        val filters = FindAllCourseInputStudent(
            category_name = Optional.present("Youtube Courses"),
            course_class = Optional.present(courseClass),
            exam_type = Optional.present(selectedExam)
        )
     //   courseViewModel.setCoursesEmpty()
        courseViewModel.fetchCourses(filters)
        observeCourses()
    }

    private fun observeCourses() {
        binding.progressBar.visibility = View.VISIBLE
        binding.rvYTCourse.visibility = View.GONE
        binding.tvShowingResults.visibility = View.GONE
        lifecycleScope.launch {
            courseViewModel.coursesState.collect { result ->
                result.onSuccess { data ->
                    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                        courseViewModel.courses.collect { result ->
                            result?.onSuccess { data ->
                                binding.progressBar.visibility = View.GONE
                                binding.rvYTCourse.visibility = View.VISIBLE
                                binding.tvShowingResults.visibility = View.VISIBLE
                                Log.e(TAG, data.toString())
                                val courseSize = data.getAllCourseForStudent.courses.size
                                val courses = data.getAllCourseForStudent.courses
                                if (courses.isEmpty()) {
                                    binding.clNoEmptyView.visibility = View.VISIBLE
                                    binding.rvYTCourse.visibility = View.GONE
                                } else if (!courses.isEmpty()) {
                                    binding.clNoEmptyView.visibility = View.GONE
                                    binding.rvYTCourse.visibility = View.VISIBLE
                                    courses.map { course ->

                                        val courseClass = course.course_class?.name ?: ""
                                        Log.e(
                                            "NEETcouseacal",
                                            helperFunctions.toDisplayString(courseClass)
                                        )
//                        when (helperFunctions.toDisplayString(courseClass)) {
//                            "11th" -> updateTabText(0, courseSize)
//                            "12th" -> updateTabText(1, courseSize)
//                            "12+" -> updateTabText(2, courseSize)
//                        }
                                        // course.toCourse()
                                    } ?: emptyList()
                                }
                                Log.d("NEETFragment", courses.toString())
                                // binding.rvYTCourse.adapter = YTCourseAdapter(courses, this@YTCourseFragment)
                            }?.onFailure { exception ->
                                // Handle the failure case
                                binding.progressBar.visibility = View.GONE
                                Log.e(TAG, exception.toString())
                            }
                        }
                    }
                }

            }
        }
    }

    override fun onFiltersSelected(selectedExam: String?, selectedSubject: String?) {
        Log.e(TAG,selectedExam + selectedSubject)
        if (selectedSubject != null) {
            fetchCoursesForClass(selectedSubject, selectedExam)
            observeCourses()
        }
    }
}