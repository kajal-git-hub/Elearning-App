package xyz.penpencil.competishun.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo3.api.Optional
import com.google.android.material.tabs.TabLayout
import com.student.competishun.curator.AllCourseForStudentQuery
import com.student.competishun.curator.type.FindAllCourseInputStudent
import xyz.penpencil.competishun.ui.adapter.CourseAdapter
import xyz.penpencil.competishun.ui.main.HomeActivity
import xyz.penpencil.competishun.ui.viewmodel.StudentCoursesViewModel
import xyz.penpencil.competishun.utils.HelperFunctions
import xyz.penpencil.competishun.utils.StudentCourseItemClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.databinding.FragmentCourseBinding


private const val TAG = "NEETFragment"
@AndroidEntryPoint
class NEETFragment : Fragment(), StudentCourseItemClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var binding: FragmentCourseBinding
    var courseListSize = ""
    val lectureCounts = mutableMapOf<String, Int>()
    private lateinit var helperFunctions: HelperFunctions
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


        (activity as? HomeActivity)?.showBottomNavigationView(false)
        (activity as? HomeActivity)?.showFloatingButton(true)

        helperFunctions = HelperFunctions()
        initializeTabLayout()
        setupTabLayout()
        setupRecyclerView()
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
                        0 ->  fetchCoursesForClass("11th")
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
            category_name = Optional.present(categoryName),
            course_class = Optional.present(courseClass),
            exam_type = Optional.present(examType),
            is_recommended = Optional.present(false)
        )
        setupTabLayout()
        courseViewModel.fetchCourses(filters)
    }

    private fun observeCourses() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            courseViewModel.courses.collect { result ->
                result?.onSuccess { data ->
                    Log.e(TAG , data.toString())
                    val courseSize = data.getAllCourseForStudent.courses.size

                    val courses = data.getAllCourseForStudent.courses.map { course ->

                        getAllLectureCount(course.id) { courseId, lectureCount ->
                            lectureCounts[courseId] = lectureCount
                            binding.recyclerView.adapter?.notifyDataSetChanged()
                        }
                        val courseClass = course.course_class?.name?:""
                       Log.e("NEETcouseacal",helperFunctions.toDisplayString(courseClass))
//                        when (helperFunctions.toDisplayString(courseClass)) {
//                            "11th" -> updateTabText(0, courseSize)
//                            "12th" -> updateTabText(1, courseSize)
//                            "12+" -> updateTabText(2, courseSize)
//                        }
                        course.toCourse()
                    } ?: emptyList()
                    Log.d("NEETFragment", courses.toString())
                    binding.recyclerView.adapter = CourseAdapter(courses,lectureCounts, this@NEETFragment)
                }?.onFailure { exception ->
                    // Handle the failure case
                    Log.e(TAG, exception.toString())
                }
            }
        }
    }

    override fun onCourseItemClicked(course: AllCourseForStudentQuery.Course,bundle: Bundle) {
        val courseTags = bundle.getStringArrayList("course_tags")?: arrayListOf()
        val bundle = Bundle().apply {
            putString("course_id", course.id)
            putStringArrayList("course_tags", courseTags)

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
            live_date = this.live_date,
            planner_description = this.planner_description,
            with_installment_price = this.with_installment_price,
            course_end_date = this.course_end_date,
        )
    }

    fun getAllLectureCount(courseId: String, callback: (String, Int) -> Unit){

        courseViewModel.fetchLectures(courseId)
        Log.e("getcourseIds",courseId)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                courseViewModel.lectures.collect { result ->
                    result?.onSuccess { lecture ->
                        val count = lecture.getAllCourseLecturesCount.lecture_count.toInt()
                        Log.e("lectureCount",count.toString())
                        callback(courseId, count)
                    }?.onFailure { exception ->
                        Log.e("LectureException", exception.toString())
                    }
                }
            }
        }
    }

    private fun updateTabText(position: Int, courseSize: Int) {
        val tabLayout = binding.studentTabLayout
        val tabText = when (position) {
            0 -> "11th ($courseSize)"
            1 -> "12th ($courseSize)"
            2 -> "12th+ ($courseSize)"
            else -> ""
        }
        tabLayout.getTabAt(position)?.text = tabText
    }

    private fun initializeTabLayout() {
        binding.studentTabLayout.getTabAt(0)?.text = "11th"
        binding.studentTabLayout.getTabAt(1)?.text = "12th"
        binding.studentTabLayout.getTabAt(2)?.text = "12th+"
    }

    companion object {
        private const val TAG = "CourseFragment"
    }
}