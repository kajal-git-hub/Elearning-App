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
import kotlinx.coroutines.launch
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.databinding.FragmentYTCourseBinding
import xyz.penpencil.competishun.ui.adapter.CourseAdapter
import xyz.penpencil.competishun.ui.adapter.YTCourseAdapter
import xyz.penpencil.competishun.ui.fragment.CourseFragment.Companion
import xyz.penpencil.competishun.ui.viewmodel.StudentCoursesViewModel
import xyz.penpencil.competishun.utils.HelperFunctions


class YTCourseFragment : Fragment() {
    private lateinit var helperFunctions: HelperFunctions
    private lateinit var binding: FragmentYTCourseBinding
    private val courseViewModel: StudentCoursesViewModel by viewModels()
    private val TAG = "YTCourseFragment"
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

        binding.tvCourseMaterialCount.text = "36 Courses"
        binding.tvShowingResults.text = "Showing results (4):"

    }

    private fun fetchCoursesForClass(courseClass: String) {
        val categoryName = arguments?.getString("category_name")
        val examType = arguments?.getString("exam_type")
        val filters = FindAllCourseInputStudent(
            category_name = Optional.present(categoryName),
            course_class = Optional.present(courseClass),
            exam_type = Optional.present(examType),
            is_recommended = Optional.present(false)
        )
        courseViewModel.setCoursesEmpty()
        courseViewModel.fetchCourses(filters)
    }

    private fun observeCourses() {
        lifecycleScope.launch {
            courseViewModel.coursesState.collect { result ->
                result.onSuccess { data ->

                    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                        courseViewModel.courses.collect { result ->
                            result?.onSuccess { data ->
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
                                Log.e(TAG, exception.toString())
                            }
                        }
                    }
                }

            }
        }
    }
}