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
import androidx.recyclerview.widget.LinearLayoutManager
import com.student.competishun.R
import com.student.competishun.curator.MyCoursesQuery
import com.student.competishun.curator.MyCoursesQuery.Folder
import com.student.competishun.curator.type.CourseStatus
import com.student.competishun.curator.type.ExamType
import com.student.competishun.curator.type.OtherRequirements
import com.student.competishun.data.model.ExploreCourse
import com.student.competishun.databinding.FragmentCourseEmptyBinding
import com.student.competishun.ui.adapter.ExploreCourseAdapter
import com.student.competishun.ui.main.HomeActivity
import com.student.competishun.ui.viewmodel.GetCourseByIDViewModel
import com.student.competishun.ui.viewmodel.MyCoursesViewModel
import com.student.competishun.ui.viewmodel.OrdersViewModel
import com.student.competishun.utils.SharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CourseEmptyFragment : Fragment() {

    private var _binding: FragmentCourseEmptyBinding? = null
    private val binding get() = _binding!!
    lateinit var sharedPreferencesManager: SharedPreferencesManager
    private val ordersViewModel: OrdersViewModel by viewModels()
    private val viewModel: MyCoursesViewModel by viewModels()
    private var coursepercent=0
    private var currentCourseId=""
    private var folderId=""
    private val getCourseByIDViewModel: GetCourseByIDViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // Handle arguments if any
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCourseEmptyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        (activity as? HomeActivity)?.showBottomNavigationView(true)
        (activity as? HomeActivity)?.showFloatingButton(false)


        sharedPreferencesManager = SharedPreferencesManager(requireContext())
        myCoursesBind()
        var userId = arguments?.getString("user_id").toString()
        if (!sharedPreferencesManager.name.isNullOrEmpty()) {
            binding.welcomeUserTxt.text = "Hello, " + sharedPreferencesManager.name
        }


       binding.profileIcon.setOnClickListener {
           findNavController().navigate(R.id.action_courseEmptyFragment_to_ProfileFragment)
       }


        // Log.e("userid  $userId: ",sharedPreferencesManager.userId.toString())
//        if (!sharedPreferencesManager.userId.isNullOrEmpty()) {
//            orderdetails(ordersViewModel,sharedPreferencesManager.userId.toString())
//        }else orderdetails(ordersViewModel,userId)

//        val exploreCourseList = listOf(
//            ExploreCourse("Prakhar Integrated (Fast Lane-2) 2024-25", "12th class", "Full-Year", "Target 2025", "Ongoing", 10),
//            ExploreCourse("Prakhar Integrated (Fast Lane-2) 2024-25", "Revision", "Full-Year", "Target 2025", "Completed", 100),
//            ExploreCourse("Prakhar Integrated (Fast Lane-2) 2024-25", "Revision", "Full-Year", "Target 2025", "Completed", 100)
//
//        )
//
//
//
//        val adapter = ExploreCourseAdapter(exploreCourseList) { course ->
//            findNavController().navigate(R.id.action_courseEmptyFragment_to_ResumeCourseFragment)
//        }
//
//        binding.rvExploreCourses.adapter = adapter
//        binding.rvExploreCourses.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

//    private fun courseDetails(orders: List<OrdersByUserIdsQuery.OrdersByUserId>) {
//        val courseDetailsList = mutableListOf<ExploreCourse>()
//        getCourseByIDViewModel.courseByID.observe(viewLifecycleOwner, Observer { course ->
//            course?.let {
//                Log.e("course", course.folder.toString())
//                var hasFreeFolder = false
//                course.folder?.forEach { folder ->
//                    if (folder.name.startsWith("Free")) {
//                        hasFreeFolder = true
//                    }
//                }
//
//                folderId = course.folder?.get(0)?.id.toString()
//
//                val courseClass = when (course.course_class.toString()) {
//                    "TWELFTH_PLUS" -> "12th+ Class"
//                    "TWELFTH" -> "12th Class"
//                    "ELEVENTH" -> "11th Class"
//                    else -> ""
//                }
//
//                val tag1 = courseClass
//                val tag2 = it.category_name.orEmpty()
//                courseDetailsList.add(
//                    ExploreCourse(
//                        it.name,
//                        tag1,
//                        tag2,
//                        "Target ${it.target_year}",
//                        it.status.toString(),
//                        coursepercent,
//                        hasFreeFolder
//                    )
//                )
//                if (courseDetailsList.size == orders.size) {
//                    binding.clEmptyMyCourse.visibility = View.GONE
//                    binding.rvExploreCourses.visibility = View.VISIBLE
//                    val adapter = ExploreCourseAdapter(courseDetailsList) { course ->
//                        val bundle = Bundle()
//                        bundle.putString("course_Id", currentCourseId)
//                        bundle.putString("folder_Id", folderId)
//                        Log.d("course_Id", currentCourseId)
//                        Log.d("folder_Idnamecouse1", folderId)
//                        findNavController().navigate(R.id.action_courseEmptyFragment_to_ResumeCourseFragment,bundle)
//                    }
//                    binding.rvExploreCourses.adapter = adapter
//                    binding.rvExploreCourses.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
//                }
//            }
//        })
//
//        orders.forEach { order ->
//            currentCourseId=order.entityId
//            Log.e("courseID",currentCourseId)
//            getCourseByIDViewModel.fetchCourseById(order.entityId)
//        }
//    }

    fun myCoursesBind() {
        binding.progressBar.visibility = View.VISIBLE
        _binding?.clEmptyMyCourse?.visibility = View.GONE
        val courseDetailsList = mutableListOf<ExploreCourse>()
        viewModel.myCourses.observe(viewLifecycleOwner) { result ->
            binding.progressBar?.visibility = View.GONE
            Log.e("getMyresule", result.toString())
            result.onSuccess { data ->
                binding.clEmptyMyCourse.visibility = View.GONE
                binding.rvExploreCourses.visibility = View.VISIBLE
                if (data.myCourses.isNotEmpty()) {
                    // Create lists to hold courses and progress
                    val courseList = mutableListOf<MyCoursesQuery.Course>()
                    val progressList = mutableListOf<MyCoursesQuery.Progress>()

                    data.myCourses.forEach { myCourse ->
                        val course = myCourse.course
                        val progress = myCourse.progress

                        val selectedCourse = MyCoursesQuery.Course(
                            course_class = course.course_class,
                            id = course.id,
                            name = course.name,
                            price = course.price,
                            target_year = course.target_year,
                            status = course.status,
                            banner_image = course.banner_image,
                            other_requirements = course.other_requirements,
                            exam_type = course.exam_type,
                            category_name = course.category_name,
                            category_id = course.category_id,
                            entity_type = course.entity_type,
                            folder = course.folder
                        )

                        val selectedProgress = MyCoursesQuery.Progress(
                            completionPercentage = progress?.completionPercentage ?: 0.0,
                            subfolderDurations = progress?.subfolderDurations,
                            folderCount = progress?.folderCount?:0.0
                        )

                        // Add course and progress to lists
                        courseList.add(selectedCourse)
                        progressList.add(selectedProgress)
                    }

                    // Create adapter with course and progress lists
                    val adapter = ExploreCourseAdapter(courseList, progressList) { course, folderIds, folderNames, subfolderDurations, folderCounts  ->
                        // Handle the course click and navigate
                        Log.d("CourseClick", "Course: ${course.name}, FolderIds: $folderIds, FolderNames: $folderNames, Progress: $folderCounts $subfolderDurations")

                        val bundle = Bundle().apply {
                            putStringArrayList("folder_ids", ArrayList(folderIds))
                            putStringArrayList("folder_names", ArrayList(folderNames))
                            putString("courseName", course.name)
                            putDouble("subfolderDurations", subfolderDurations)
                            putStringArrayList("folderCounts", ArrayList(folderCounts))
                        }

                        findNavController().navigate(R.id.action_courseEmptyFragment_to_ResumeCourseFragment, bundle)
                    }

                    binding.rvExploreCourses.adapter = adapter
                    binding.rvExploreCourses.layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                }
            }.onFailure {
                Log.e("MyCoursesFail", it.message.toString())
                Toast.makeText(requireContext(), "Failed to load courses: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.fetchMyCourses()
    }



//    fun orderdetails(ordersViewModel: OrdersViewModel, userId:String
//    ){
//        val userIds = listOf(userId)
//        ordersViewModel.fetchOrdersByUserIds(userIds)
//        ordersViewModel.ordersByUserIds.observe(viewLifecycleOwner, Observer { orders ->
//
//            Log.d("orders",orders.toString())
//
//            binding.clEmptyMyCourse.visibility = View.VISIBLE
//            binding.welcomeUserTxt.text = "Hello, "+sharedPreferencesManager.name
//            orders?.let {
//                courseDetails(orders)
//            } ?: run {
//                binding.clEmptyMyCourse.visibility = View.VISIBLE
//                Log.e("courseEmpty", "No orders found")
//            }
//        })
//
//        // Fetch orders by user IDs
//
//    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
