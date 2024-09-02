package com.student.competishun.ui.fragment

import RecommendViewAllAdapter
import RecommendedCoursesAdapter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.apollographql.apollo3.api.Optional
import com.student.competishun.R
import com.student.competishun.curator.type.CourseStatus
import com.student.competishun.curator.type.FindAllCourseInput
import com.student.competishun.databinding.FragmentRecommendViewDetailBinding
import com.student.competishun.ui.viewmodel.CoursesViewModel
import dagger.hilt.android.AndroidEntryPoint

import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.student.competishun.curator.AllCourseForStudentQuery
import com.student.competishun.curator.type.FindAllCourseInputStudent
import com.student.competishun.data.model.PromoBannerModel
import com.student.competishun.ui.adapter.PromoBannerAdapter
import com.student.competishun.ui.viewmodel.StudentCoursesViewModel
import com.student.competishun.ui.viewmodel.UserViewModel
import com.student.competishun.utils.Constants

@AndroidEntryPoint
class RecommendViewDetail : Fragment() {

    private val userViewModel: UserViewModel by viewModels()

    private val studentCoursesViewModel: StudentCoursesViewModel by viewModels()
    var courseType:String = ""

    private var _binding: FragmentRecommendViewDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: RecommendViewAllAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecommendViewDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.appbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        setupToolbar()
        getAllCoursesForStudent(courseType)

//        adapter = RecommendViewAllAdapter(emptyList()) { selectedCourse ->
//            val bundle = Bundle().apply {
//                putString("course_id", selectedCourse.id)
//            }
//            findNavController().navigate(R.id.exploreFragment, bundle)
//        }
//
//        binding.rvRecommendedCourses.adapter = adapter
//        binding.rvRecommendedCourses.layoutManager =
//            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
//
//        coursesViewModel.courses.observe(viewLifecycleOwner, Observer { courses ->
//            if (courses != null) {
//                adapter.updateData(courses)
//            }
//        })
//
//        val filters = FindAllCourseInput(
//            exam_type = Optional.Absent,
//            is_recommended = Optional.present(true),
//            course_status = Optional.present(listOf(CourseStatus.PUBLISHED)),
//            limit = Optional.present(20)
//        )
//        coursesViewModel.fetchCourses(filters)
    }

    fun getAllCoursesForStudent(courseType: String) {
        if (courseType!="IIT-JEE"|| courseType!="NEET" ){
            this.courseType ="IIT-JEE"
        }
        val filters = FindAllCourseInputStudent(
            Optional.Absent,Optional.Absent,
            Optional.present(courseType),Optional.present(true))
        studentCoursesViewModel.fetchCourses(filters)

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            studentCoursesViewModel.courses.collect { result ->
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
                            with_installment_price = course.with_installment_price,
                            course_end_date = course.course_end_date,
                            banners = course.banners
                        )
                    } ?: emptyList()

                    val bannerList = mutableListOf<PromoBannerModel>()

                    courses?.forEach { course ->
                        Log.e("Course", course.toString())
                        Log.e("bannersList",course.banners.toString())
                        bannerList.add(PromoBannerModel(course.banner_image))
                    }

                    binding.rvRecommendedCourses.adapter = courses?.let { courseList ->
                        RecommendedCoursesAdapter(courseList) { selectedCourse ->
                            val bundle = Bundle().apply {
                                putString("course_id", selectedCourse.id)
                            }
                            findNavController().navigate(R.id.exploreFragment,bundle)
                        }
                    }
                    binding.rvRecommendedCourses.layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)


                }?.onFailure { exception ->
                    // Handle the failure case
                    Log.e("gettiStudentfaik",exception.toString())
                }
            }
        }
    }


    private fun setupToolbar() {
        val searchView = binding.appbar.menu.findItem(R.id.action_search).actionView as SearchView

        searchView.queryHint = "Search Courses"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        })
    }

    fun getMyDetails() {
        userViewModel.fetchUserDetails()
        userViewModel.userDetails.observe(viewLifecycleOwner) { result ->
            result.onSuccess { data ->
                val userDetails = data.getMyDetails
                courseType = userDetails.userInformation.preparingFor?:""

                Log.e("courseeTypehome",courseType)

            }.onFailure { exception ->
                Toast.makeText(requireContext(), "Error fetching details: ${exception.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}

