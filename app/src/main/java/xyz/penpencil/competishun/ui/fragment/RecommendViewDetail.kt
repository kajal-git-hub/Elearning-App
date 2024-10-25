package xyz.penpencil.competishun.ui.fragment

import RecommendViewAllAdapter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.apollographql.apollo3.api.Optional
import dagger.hilt.android.AndroidEntryPoint
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import com.student.competishun.curator.AllCourseForStudentQuery
import com.student.competishun.curator.type.FindAllCourseInputStudent
import xyz.penpencil.competishun.ui.main.HomeActivity
import xyz.penpencil.competishun.ui.viewmodel.StudentCoursesViewModel
import xyz.penpencil.competishun.ui.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.databinding.FragmentRecommendViewDetailBinding

@AndroidEntryPoint
class RecommendViewDetail : DrawerVisibility() {

    private val userViewModel: UserViewModel by viewModels()
    private val studentCoursesViewModel: StudentCoursesViewModel by viewModels()
    private var courseType: String = ""
    private var _binding: FragmentRecommendViewDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: RecommendViewAllAdapter

    private var courseTypeRecommend = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecommendViewDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? HomeActivity)?.showBottomNavigationView(false)
        (activity as? HomeActivity)?.showFloatingButton(false)

        adapter = RecommendViewAllAdapter(emptyList()) { selectedCourse, recommendCourseTags ->
            val bundle = Bundle().apply {
                putString("course_id", selectedCourse.id)
                putStringArrayList("recommendCourseTags", ArrayList(recommendCourseTags))
            }
            findNavController().navigate(R.id.exploreFragment, bundle)
        }

        binding.rvRecommendedCourses.layoutManager = LinearLayoutManager(context)
        binding.rvRecommendedCourses.adapter = adapter

        setupToolbar()

        getMyDetails()

        binding.appbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun getAllCoursesForStudent(courseType: String) {
        var courseTypes = courseType
        Log.d("courseTypes", courseTypes)
        if (courseType != "IIT-JEE" && courseType != "NEET") {
            courseTypes = "IIT-JEE"
        }
        Log.d("courseTypeRecommend", courseTypes)

        val filters = FindAllCourseInputStudent(
            category_name = Optional.Absent,
            course_class = Optional.Absent,
            exam_type = Optional.present(courseTypes),
            is_recommended = Optional.present(true),
            sortOrder = Optional.present("DESC")
        )
        studentCoursesViewModel.fetchCourses(filters)
        binding.progressBarRec.visibility = View.VISIBLE
        binding.rvRecommendedCourses.visibility = View.GONE

        viewLifecycleOwner.lifecycleScope.launch {
            studentCoursesViewModel.courses.collect { result ->
                result?.onSuccess { data ->
                    Log.d("Getting Student Courses", data.toString())
                    val courses = data.getAllCourseForStudent.courses.map { course ->
                        courseTypeRecommend = course.course_type.toString()
                        AllCourseForStudentQuery.Course(
                            discount = course.discount,
                            name = course.name,
                            course_start_date = course.course_start_date,
                            course_validity_end_date = course.course_validity_end_date,
                            price = course.price,
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
                            live_date = course.live_date,
                            planner_description = course.planner_description,
                            with_installment_price = course.with_installment_price,
                            course_end_date = course.course_end_date,
                        )
                    }

                    // Update the adapter with the fetched courses
                    adapter.updateData(courses)

                    // Update visibility
                    binding.progressBarRec.visibility = View.GONE
                    binding.rvRecommendedCourses.visibility = View.VISIBLE

                }?.onFailure { exception ->
                    Log.e("Student Courses Failed", exception.toString())
                    Toast.makeText(requireContext(), "Error fetching courses: ${exception.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setupToolbar() {
        val searchView = binding.appbar.menu.findItem(R.id.action_search)?.actionView as? SearchView
        searchView?.queryHint = "Search Courses"
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        })
    }

    private fun getMyDetails() {
        userViewModel.fetchUserDetails()
        userViewModel.userDetails.observe(viewLifecycleOwner) { result ->
            result.onSuccess { data ->
                val userDetails = data.getMyDetails
                courseType = userDetails.userInformation.preparingFor ?: ""
                Log.d("CourseType Retrieved", courseType)

                // Call getAllCoursesForStudent() only after courseType is received
                if (courseType.isNotEmpty()) {
                    getAllCoursesForStudent(courseType)
                } else {
                    // If no courseType, default to "IIT-JEE"
                    getAllCoursesForStudent("IIT-JEE")
                }
            }.onFailure { exception ->
                Toast.makeText(requireContext(), "Error fetching details: ${exception.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Prevent memory leaks
    }
}
