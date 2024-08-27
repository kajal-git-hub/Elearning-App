package com.student.competishun.ui.fragment

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
import com.apollographql.apollo3.api.or
import com.student.competishun.R
import com.student.competishun.coinkeeper.OrdersByUserIdsQuery
import com.student.competishun.data.model.ExploreCourse
import com.student.competishun.databinding.FragmentCourseEmptyBinding
import com.student.competishun.ui.adapter.ExploreCourseAdapter
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
    private val viewModel: MyCoursesViewModel  by viewModels()
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
        myCourses()
        sharedPreferencesManager = SharedPreferencesManager(requireContext())
        var userId = arguments?.getString("user_id").toString()
         Log.e("userid  $userId: ",sharedPreferencesManager.userId.toString())
        if (!sharedPreferencesManager.userId.isNullOrEmpty()) {
            orderdetails(ordersViewModel,sharedPreferencesManager.userId.toString())
        }else orderdetails(ordersViewModel,userId)

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

    private fun courseDetails(orders: List<OrdersByUserIdsQuery.OrdersByUserId>) {
        val courseDetailsList = mutableListOf<ExploreCourse>()

        getCourseByIDViewModel.courseByID.observe(viewLifecycleOwner, Observer { course ->
            course?.let {
                var courseClass = ""
                if(course.course_class.toString() =="TWELFTH_PLUS"){
                    courseClass = "12th+ Class"

                }else if(course.course_class.toString()=="TWELFTH"){
                    courseClass = "12th Class"

                }
                else if(course.course_class.toString()=="ELEVENTH"){
                    courseClass = "11th Class"

                }
                val tag1 = courseClass
                val tag2 = it.category_name.orEmpty()
                courseDetailsList.add(
                    ExploreCourse(
                        it.name,
                        tag1,
                        tag2,
                        "Target ${it.target_year}",
                        it.status.toString(),
                        10
                    )
                )

               if (courseDetailsList.size == orders.size) {
                    binding.clEmptyMyCourse.visibility = View.GONE
                    binding.rvExploreCourses.visibility = View.VISIBLE
                    val adapter = ExploreCourseAdapter(courseDetailsList) { course ->
                        findNavController().navigate(R.id.action_courseEmptyFragment_to_ResumeCourseFragment)
                    }
                    binding.rvExploreCourses.adapter = adapter
                    binding.rvExploreCourses.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                }
            }
        })

        orders.forEach { order ->
            getCourseByIDViewModel.fetchCourseById(order.entityId)
        }
    }



    fun orderdetails(ordersViewModel: OrdersViewModel, userId:String
    ){
        val userIds = listOf(userId)
        ordersViewModel.fetchOrdersByUserIds(userIds)
        ordersViewModel.ordersByUserIds.observe(viewLifecycleOwner, Observer { orders ->

            binding.clEmptyMyCourse.visibility = View.VISIBLE
            binding.welcomeUserTxt.text = "Hello, "+sharedPreferencesManager.name
            orders?.let {
                courseDetails(orders)
            } ?: run {
                binding.clEmptyMyCourse.visibility = View.VISIBLE
                Log.e("courseEmpty", "No orders found")
            }
        })

        // Fetch orders by user IDs

    }

    fun myCourses(){
        viewModel.myCourses.observe(viewLifecycleOwner) { result ->
            result.onSuccess { data ->
                val courses = data.myCourses?.map { it.course }
                Log.e("getMyCourses",courses.toString())
            }.onFailure {
                Log.e("MyCoursesFail",it.message.toString())
                Toast.makeText(requireContext(), "Failed to load courses: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.fetchMyCourses()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
