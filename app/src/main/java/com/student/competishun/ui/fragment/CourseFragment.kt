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
import com.student.competishun.databinding.FragmentCoursesBinding
import com.student.competishun.databinding.FragmentExploreBinding
import com.student.competishun.databinding.FragmentHomeBinding
import com.student.competishun.databinding.FragmentMyCartBinding
import com.student.competishun.ui.adapter.CourseAdapter
import com.student.competishun.ui.viewmodel.StudentCoursesViewModel
import com.student.competishun.utils.StudentCourseItemClickListener
import dagger.hilt.android.AndroidEntryPoint


private const val TAG = "CourseFragment"

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
    ): View? {
        val view = inflater.inflate(R.layout.fragment_course, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

      //  recyclerView.adapter = CourseAdapter(combinedTabItems)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val categoryName = arguments?.getString("category_name")
        // Fetch courses with filters
        val filters = FindAllCourseInputStudent(Optional.present(categoryName),Optional.present("12th"),Optional.present("IIT-JEE"),Optional.present(null))
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
                    recyclerView.adapter = CourseAdapter(courses,this@CourseFragment)
                }?.onFailure { exception ->
                    // Handle the failure case
                    Log.e("gettiStudentfaik",exception.toString())
                }
            }
        }
    }

    fun tabselection(){
//        binding.studentTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//            override fun onTabSelected(tab: TabLayout.Tab?) {
//                tab?.let {
//                    val filters = when (it.position) {
//                        0 -> {
//                            // 11th selected
//                            FindAllCourseInputStudent(
//                                Optional.present("11th"),
//                                Optional.present("IIT-JEE"),
//                                Optional.present(null),
//                                Optional.present(null)
//                            )
//                        }
//                        1 -> {
//                            // 12th selected
//                            FindAllCourseInputStudent(
//                                Optional.present("12th"),
//                                Optional.present("IIT-JEE"),
//                                Optional.present(null),
//                                Optional.present(null)
//                            )
//                        }
//                        2 -> {
//                            // 12th+ selected
//                            FindAllCourseInputStudent(
//                                Optional.present("12th+"),
//                                Optional.present("IIT-JEE"),
//                                Optional.present(null),
//                                Optional.present(null)
//                            )
//                        }
//                        else -> {
//                            // Default or fallback case
//                            FindAllCourseInputStudent(
//                                Optional.present(null),
//                                Optional.present("IIT-JEE"),
//                                Optional.present(null),
//                                Optional.present(null)
//                            )
//                        }
//                    }
//
//                    // Fetch and filter courses based on the selected tab
//                    courseViewModel.fetchCourses(filters)
//                }
//            }
//
//            override fun onTabUnselected(tab: TabLayout.Tab?) {
//                // Handle if needed
//            }
//
//            override fun onTabReselected(tab: TabLayout.Tab?) {
//                // Handle if needed
//            }
//        })
    }

    override fun onCourseItemClicked(course: AllCourseForStudentQuery.Course) {
        val bundle = Bundle().apply {
            putString("course_id", course.id)
        }
        Log.e("coursseID",course.id.toString())
        findNavController().navigate(R.id.action_coursesFragment_to_ExploreFragment,bundle)

    }

}