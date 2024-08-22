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
import com.student.competishun.R
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo3.api.Optional
import com.student.competishun.curator.AllCourseForStudentQuery
import com.student.competishun.curator.type.FindAllCourseInputStudent
import com.student.competishun.ui.adapter.CourseAdapter
import com.student.competishun.ui.viewmodel.StudentCoursesViewModel
import com.student.competishun.utils.StudentCourseItemClickListener
import dagger.hilt.android.AndroidEntryPoint


private const val TAG = "NEETFragment"
@AndroidEntryPoint
class NEETFragment : Fragment(), StudentCourseItemClickListener {
    private lateinit var recyclerView: RecyclerView
    private val courseViewModel: StudentCoursesViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_course, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val tabItems = listOf(
            com.student.competishun.data.model.TabItem(
                discount = "11% OFF",
                courseName = "Prakhar Integrated (Fast Lane-2) 2024-25",
                tags = listOf("12th Class", "Full-Year", "Target 2025"),
                startDate = "Starts On: 01 Jul, 24",
                endDate = "Expiry Date: 31 Jul, 24",
                lectures = "Lectures: 56",
                quizzes = "Quiz & Tests: 120",
                originalPrice = "₹44,939",
                discountPrice = "₹29,900"
            )
            // Add more TabItem objects here
        )
      //  recyclerView.adapter = CourseAdapter(tabItems)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val categoryName = arguments?.getString("category_name")
        val filters = FindAllCourseInputStudent(
            Optional.present(categoryName),
            Optional.present("NEET"),
            Optional.present(null),
            Optional.present(null))
        courseViewModel.fetchCourses(filters)

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            courseViewModel.courses.collect { result ->
                result?.onSuccess { data ->
                    Log.e("gettiStudent",data.toString())
                    val courses = data.getAllCourseForStudent.courses.map {
                            course ->
                        AllCourseForStudentQuery.Course(
                            discount = course.discount,
                            name = course.name,
                            course_start_date = course.course_start_date,
                            course_validity_end_date = course.course_validity_end_date,
                            price =course.price,
                            target_year = course.target_year,
                            id = course.id,
                            academic_year = course.academic_year,
                            complementary_course = course.complementary_course,
                            course_features = course.course_features,
                            course_class = course.course_class,
                            course_tags = course.course_tags,
                            banner_image = course.banner_image,
                            status = course.status,
                            category_id = course.category_id,
                            category_name = course.category_name,
                            course_primary_teachers = course.course_primary_teachers,
                            course_support_teachers = course.course_support_teachers,
                            course_type = course.course_type,
                            entity_type = course.entity_type,
                            exam_type = course.exam_type,
                            planner_description = course.planner_description,
                            with_installment_price = course.with_installment_price
                        )
                    } ?: emptyList()
                    recyclerView.adapter = CourseAdapter(courses,this@NEETFragment)
                }?.onFailure { exception ->
                    // Handle the failure case
                    Log.e("gettiStudentfaik",exception.toString())
                }
            }
        }
    }

    override fun onCourseItemClicked(course: AllCourseForStudentQuery.Course) {
        findNavController().navigate(R.id.action_coursesFragment_to_ExploreFragment)

    }

}