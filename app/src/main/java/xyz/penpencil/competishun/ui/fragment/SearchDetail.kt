package xyz.penpencil.competishun.ui.fragment

import RecommendViewAllAdapter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import xyz.penpencil.competishun.databinding.FragmentSearchBinding

@AndroidEntryPoint
class SearchDetail : DrawerVisibility() {

    private val userViewModel: UserViewModel by viewModels()
    private val studentCoursesViewModel: StudentCoursesViewModel by viewModels()
    private var courseType: String = ""
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: RecommendViewAllAdapter

    private var courseTypeRecommend = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
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
        binding.appbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.homeFragment)
        }
        var searchQuery = arguments?.getString("searchQuery")
        binding.rvRecommendedCourses.layoutManager = LinearLayoutManager(context)
        binding.rvRecommendedCourses.adapter = adapter
        if (searchQuery != null) {
            binding.tittleTb.setText(searchQuery)
            getAllCoursesForStudent(searchQuery)
        }
        setupTextWatchers()

    }

    private fun setupTextWatchers() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val typedText = s?.toString()?.trim() ?: ""

                // Check if the length is at least 3 characters
                if (typedText.isNotEmpty()) {
                    // Call your function with the typed text
                    getAllCoursesForStudent(typedText)
                    binding.rvRecommendedCourses.visibility = View.GONE
                } else {
                    binding.rvRecommendedCourses.visibility = View.GONE
                    binding.llEmpty.visibility = View.VISIBLE
                    binding.progressBarRec.visibility = View.GONE
                    binding.coursesCountTv.visibility = View.GONE
                    binding.tvCourseNotExits.visibility = View.GONE
                }
            }
        }
        binding.tittleTb.addTextChangedListener(textWatcher)
    }

    private fun getAllCoursesForStudent(searchQuery: String) {
        var search = searchQuery
        Log.d("courseTypeRecommend", search)

        val filters = FindAllCourseInputStudent(
            search = Optional.present(search),
        )
        studentCoursesViewModel.fetchCourses(filters)
        binding.progressBarRec.visibility = View.VISIBLE
        binding.rvRecommendedCourses.visibility = View.GONE

        viewLifecycleOwner.lifecycleScope.launch {
            studentCoursesViewModel.courses.collect { result ->
                result?.onSuccess { data ->
                    Log.d("Getting Student Courses", data.toString())
                    val courseList = data.getAllCourseForStudent.courses
                    if (courseList.isEmpty()){

                        binding.rvRecommendedCourses.visibility = View.GONE
                        binding.llEmpty.visibility = View.VISIBLE
                        binding.progressBarRec.visibility = View.GONE
                        binding.coursesCountTv.visibility = View.GONE
                        binding.tvCourseNotExits.text = getString(R.string.course_not_exits, searchQuery)
                    } else {
                        binding.rvRecommendedCourses.visibility = View.VISIBLE
                        binding.llEmpty.visibility = View.GONE
                        binding.coursesCountTv.visibility = View.VISIBLE
                        binding.coursesCountTv.text = getString(R.string.showing_results, courseList.size.toString())
                        val courses = courseList.map { course ->
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
                    }
                }?.onFailure { exception ->
                    Log.e("Student Courses Failed", exception.toString())
                    Toast.makeText(requireContext(), "Error fetching courses: ${exception.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Prevent memory leaks
    }
}
