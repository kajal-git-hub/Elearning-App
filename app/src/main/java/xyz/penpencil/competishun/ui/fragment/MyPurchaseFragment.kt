package xyz.penpencil.competishun.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.data.model.CoursePaymentDetails
import xyz.penpencil.competishun.databinding.FragmentMyPurchaseBinding
import xyz.penpencil.competishun.ui.adapter.CoursePaymentAdapter
import xyz.penpencil.competishun.ui.adapter.PurchaseStatus
import xyz.penpencil.competishun.ui.adapter.PurchaseStatusAdapter
import xyz.penpencil.competishun.ui.main.HomeActivity
import xyz.penpencil.competishun.ui.viewmodel.UserViewModel
import xyz.penpencil.competishun.utils.HelperFunctions

@AndroidEntryPoint
class MyPurchaseFragment : DrawerVisibility() {
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
    ): View {
        _binding = FragmentMyPurchaseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.setFocusableInTouchMode(true)
        view.requestFocus()
        view.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    v?.findNavController()?.popBackStack()
                    return true
                }
                return false
            }

        })

        observeUserDetails()
        userViewModel.fetchUserDetails()

        (activity as? HomeActivity)?.showBottomNavigationView(false)
        (activity as? HomeActivity)?.showFloatingButton(false)

        binding.etBTPurchase.setOnClickListener {
            findNavController().popBackStack()
        }


        val statusList = listOf(
            PurchaseStatus("All"),
            PurchaseStatus("Successful"),
//            PurchaseStatus("Failed"),
//            PurchaseStatus("Processing")
        )

        binding.rvToggleButtons.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = PurchaseStatusAdapter(context, statusList) { selectedStatus ->
                filterPurchaseList(selectedStatus)

            }
        }

        coursePaymentAdapter = CoursePaymentAdapter(emptyList()) { selectedCourse ->
            val bundle = Bundle().apply {
                putString("PurchaseCourseId", selectedCourse.enrolledCourseId)
                putString("PurchaseUserId", selectedCourse.userId)
                putString("FirstPurchase", selectedCourse.amountPaidOn)
            }
            findNavController().navigate(R.id.MyPurchaseDetail, bundle)


        }
        binding.recyclerViewPurchases.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = coursePaymentAdapter
        }
    }

    private fun observeUserDetails() {
        userViewModel.userDetails.observe(viewLifecycleOwner) { result ->
            result.onSuccess { data ->
                val paymentDetails = data.getMyDetails.courses.mapNotNull { course ->
                    Log.d("paymentType", course?.paymentType.toString())
                    course?.let {
                        CoursePaymentDetails(
                            purchaseStatus = it.paymentStatus ?: "",
                            courseName = it.enrolledCourseName ?: "",
                            statusIconRes = getIconResForStatus(it.paymentStatus),
                            totalAmountPaid = "₹ ${it.pricePaid}",
                            amountPaidOn = helperFunctions.formatCourseDate(it.createdAt.toString()),
                            paymentType = it.paymentType.toString(),
                            studentRollNo = data.getMyDetails.userInformation.rollNumber.toString(),
                            isRefundVisible = it.paymentStatus == "refund complete",
                            enrolledCourseId = it.enrolledCourseId,
                            userId = data.getMyDetails.id
                        )
                    }
                }

                coursePaymentList = paymentDetails
                // Initially show all payments
                coursePaymentAdapter.updateData(coursePaymentList)
            }.onFailure { exception ->
                Toast.makeText(
                    requireContext(),
                    "Error fetching details: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun getIconResForStatus(paymentStatus: String?): Int {
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
