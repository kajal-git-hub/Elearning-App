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
import com.student.competishun.data.model.CoursePaymentDetails
import com.student.competishun.databinding.FragmentMyPurchaseBinding
import com.student.competishun.ui.adapter.CoursePaymentAdapter
import com.student.competishun.ui.adapter.PurchaseStatus
import com.student.competishun.ui.adapter.PurchaseStatusAdapter
import com.student.competishun.ui.main.HomeActivity
import com.student.competishun.ui.viewmodel.UserViewModel
import com.student.competishun.utils.HelperFunctions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyPurchaseFragment : Fragment() {
    private var _binding: FragmentMyPurchaseBinding? = null
    private val binding get() = _binding!!
    private lateinit var coursePaymentAdapter: CoursePaymentAdapter
    private lateinit var coursePaymentList: List<CoursePaymentDetails>
    private val userViewModel: UserViewModel by viewModels()
    private val helperFunctions = HelperFunctions()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
//            formatCourseDate
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyPurchaseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeUserDetails()
        userViewModel.fetchUserDetails()

        (activity as? HomeActivity)?.showBottomNavigationView(false)
        (activity as? HomeActivity)?.showFloatingButton(false)

        binding.MyPurchaseTopView.setOnClickListener {
            findNavController().navigate(R.id.ProfileFragment)
        }


        val statusList = listOf(
            PurchaseStatus("All"),
            PurchaseStatus("Successful"),
//            PurchaseStatus("Failed"),
//            PurchaseStatus("Processing")
        )

        binding.rvToggleButtons.apply {
            layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
            adapter = PurchaseStatusAdapter(context, statusList){ selectedStatus ->
                filterPurchaseList(selectedStatus)

            }
        }



//        coursePaymentList = listOf(
//            CoursePaymentDetails(
//                "COMPLETE",
//                "Prakhar Integrated (Fast Lane-2) 2024-25",
//                R.drawable.group_1707479053,
//                "₹ 32,500",
//                "26 July, 2024",
//                "One-Time Payment",
//                "24111001",
//                false // Refund note visible
//            ),
//            CoursePaymentDetails(
//                "FAILED",
//                "Prakhar Integrated (Fast Lane-2) 2024-25",
//                R.drawable.failed_status,
//                "₹ 32,500",
//                "26 July, 2024",
//                "One-Time Payment",
//                "24111001",
//                false // Refund note visible
//            ),
//            CoursePaymentDetails(
//                "REFUND COMPLETE",
//                "Prakhar Integrated (Fast Lane-2) 2024-25",
//                R.drawable.refund_status,
//                "₹ 32,500",
//                "26 July, 2024",
//                "One-Time Payment",
//                "24111001",
//                true // Refund note visible
//            ),
//            CoursePaymentDetails(
//                "PAYMENT PROCESSING",
//                "Prakhar Integrated (Fast Lane-2) 2024-25",
//                R.drawable.process_status,
//                "₹ 32,500",
//                "26 July, 2024",
//                "One-Time Payment",
//                "24111001",
//                true // Refund note visible
//            ),
//        )

        coursePaymentAdapter = CoursePaymentAdapter(emptyList())
        binding.recyclerViewPurchases.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = coursePaymentAdapter
        }
    }
    private fun observeUserDetails() {
        userViewModel.userDetails.observe(viewLifecycleOwner) { result ->
            result.onSuccess { data ->
                // Map the courses data to the CoursePaymentDetails model
                coursePaymentList = data.getMyDetails.courses.map { course ->
                    CoursePaymentDetails(
                        purchaseStatus = course?.paymentStatus ?: "",
                        courseName = course?.enrolledCourseName ?: "",
                        statusIconRes = getStatusIcon(course?.paymentStatus),
                        totalAmountPaid = "₹ ${course?.pricePaid}",
                        amountPaidOn = (  helperFunctions.formatCourseDate((course?.createdAt ?: "").toString())),
                        paymentType = "One-Time Payment", // Assuming payment type is same
                        studentRollNo = "24111001", // Placeholder for roll number
                        isRefundVisible = course?.paymentStatus == "REFUND COMPLETE"
                    )
                }

                // Initially show all data
                coursePaymentAdapter.updateData(coursePaymentList)
            }.onFailure { exception ->
                Toast.makeText(requireContext(), "Error fetching details: ${exception.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun getStatusIcon(paymentStatus: String?): Int {
        return when (paymentStatus) {
            "captured" -> R.drawable.group_1707479053
            "FAILED" -> R.drawable.failed_status
            "REFUND COMPLETE" -> R.drawable.refund_status
            "PAYMENT PROCESSING" -> R.drawable.process_status
            else -> R.drawable.group_1707479053 // Fallback icon
        }
    }
    private fun filterPurchaseList(selectedStatus: String) {
        val filteredList = when (selectedStatus) {
            "All" -> coursePaymentList
            "Successful" -> coursePaymentList.filter { it.purchaseStatus == "captured" }
//            "Failed" -> coursePaymentList.filter { it.purchaseStatus == "FAILED" }
//            "Processing" -> coursePaymentList.filter { it.purchaseStatus == "PAYMENT PROCESSING" }
            else -> coursePaymentList
        }

        // Update the adapter with the filtered list
        coursePaymentAdapter.updateData(filteredList)
    }

}