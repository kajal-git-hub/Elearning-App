package com.student.competishun.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo3.api.Optional
import com.google.android.material.tabs.TabLayout
import com.student.competishun.R
import com.student.competishun.curator.AllCourseForStudentQuery
import com.student.competishun.curator.type.FindAllCourseInputStudent
import com.student.competishun.data.model.TabItem
import com.student.competishun.databinding.FragmentCourseBinding
import com.student.competishun.ui.adapter.CourseAdapter
import com.student.competishun.ui.viewmodel.StudentCoursesViewModel
import com.student.competishun.utils.StudentCourseItemClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CourseFragment : Fragment(), StudentCourseItemClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var binding: FragmentCourseBinding
    private val courseViewModel: StudentCoursesViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCourseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupTabLayout()
        fetchCoursesForClass("11th")
        observeCourses()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun setupTabLayout() {
        binding.studentTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    when (it.position) {
                        0 -> fetchCoursesForClass("11th")
                        1 -> fetchCoursesForClass("12th")
                        2 -> fetchCoursesForClass("12+")
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Handle tab unselected if needed
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Handle tab reselected if needed
            }
        })
    }

    private fun fetchCoursesForClass(courseClass: String) {
        val categoryName = arguments?.getString("category_name")
        val examType = arguments?.getString("exam_type")
        val filters = FindAllCourseInputStudent(
            Optional.present(categoryName),
            Optional.present(courseClass),
            Optional.present(examType),
            Optional.present(null)
        )
        courseViewModel.fetchCourses(filters)
    }

    private fun observeCourses() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            courseViewModel.courses.collect { result ->
                result?.onSuccess { data ->
                    Log.e(TAG, data.toString())
                    val courses = data.getAllCourseForStudent.courses.map { course ->
                        course.toCourse()
                    } ?: emptyList()
                    Log.d("courseFragment", courses.toString())
                    binding.recyclerView.adapter = CourseAdapter(courses, this@CourseFragment)
                }?.onFailure { exception ->
                    // Handle the failure case
                    Log.e(TAG, exception.toString())
                }
            }
        }
    }

    override fun onCourseItemClicked(course: AllCourseForStudentQuery.Course) {
        val bundle = Bundle().apply {
            putString("course_id", course.id)
        }
        Log.e(TAG, course.id.toString())
        findNavController().navigate(R.id.action_coursesFragment_to_ExploreFragment, bundle)
    }

    private fun AllCourseForStudentQuery.Course.toCourse(): AllCourseForStudentQuery.Course {
        return AllCourseForStudentQuery.Course(
            discount = this.discount,
            name = this.name,
            course_start_date = this.course_start_date,
            course_validity_end_date = this.course_validity_end_date,
            price = this.price,
            target_year = this.target_year,
            id = this.id,
            academic_year = this.academic_year,
            complementary_course = this.complementary_course,
            course_features = this.course_features,
            course_class = this.course_class,
            course_tags = this.course_tags,
            banner_image = this.banner_image,
            status = this.status,
            category_id = this.category_id,
            category_name = this.category_name,
            course_primary_teachers = this.course_primary_teachers,
            course_support_teachers = this.course_support_teachers,
            course_type = this.course_type,
            entity_type = this.entity_type,
            exam_type = this.exam_type,
            planner_description = this.planner_description,
            with_installment_price = this.with_installment_price
        )
    }

    companion object {
        private const val TAG = "CourseFragment"
    }
}