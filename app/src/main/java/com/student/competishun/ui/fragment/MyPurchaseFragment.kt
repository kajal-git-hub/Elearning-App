package com.student.competishun.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.student.competishun.R
import com.student.competishun.data.model.CoursePaymentDetails
import com.student.competishun.databinding.FragmentMyPurchaseBinding
import com.student.competishun.ui.adapter.CoursePaymentAdapter
import com.student.competishun.ui.adapter.PurchaseStatus
import com.student.competishun.ui.adapter.PurchaseStatusAdapter
import com.student.competishun.ui.main.HomeActivity

class MyPurchaseFragment : Fragment() {
    private var _binding: FragmentMyPurchaseBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
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

        (activity as? HomeActivity)?.showBottomNavigationView(false)
        (activity as? HomeActivity)?.showFloatingButton(false)

        val statusList = listOf(
            PurchaseStatus("All"),
            PurchaseStatus("Successful"),
            PurchaseStatus("Failed"),
            PurchaseStatus("Processing")
        )

        binding.rvToggleButtons.apply {
            layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
            adapter = PurchaseStatusAdapter(context, statusList)
        }

        val coursePaymentList = listOf(
            CoursePaymentDetails(
                "COMPLETE",
                "Prakhar Integrated (Fast Lane-2) 2024-25",
                R.drawable.group_1707479053,
                "₹ 32,500",
                "26 July, 2024",
                "One-Time Payment",
                "24111001",
                false // Refund note visible
            ),
            CoursePaymentDetails(
                "FAILED",
                "Prakhar Integrated (Fast Lane-2) 2024-25",
                R.drawable.failed_status,
                "₹ 32,500",
                "26 July, 2024",
                "One-Time Payment",
                "24111001",
                false // Refund note visible
            ),
            CoursePaymentDetails(
                "REFUND COMPLETE",
                "Prakhar Integrated (Fast Lane-2) 2024-25",
                R.drawable.refund_status,
                "₹ 32,500",
                "26 July, 2024",
                "One-Time Payment",
                "24111001",
                true // Refund note visible
            ),
            CoursePaymentDetails(
                "PAYMENT PROCESSING",
                "Prakhar Integrated (Fast Lane-2) 2024-25",
                R.drawable.process_status,
                "₹ 32,500",
                "26 July, 2024",
                "One-Time Payment",
                "24111001",
                true // Refund note visible
            ),
        )


        binding.recyclerViewPurchases.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = CoursePaymentAdapter(coursePaymentList)
        }

    }

}